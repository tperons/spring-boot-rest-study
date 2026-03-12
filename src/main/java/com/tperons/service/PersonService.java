package com.tperons.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tperons.controller.PersonController;
import com.tperons.dto.PersonDTO;
import com.tperons.entity.Person;
import com.tperons.exception.BadRequestException;
import com.tperons.exception.RequiredObjectIsNullException;
import com.tperons.exception.ResourceNotFoundException;
import com.tperons.file.exporter.contract.PersonExporter;
import com.tperons.file.exporter.factory.FileExporterFactory;
import com.tperons.file.importer.contract.FileImporter;
import com.tperons.file.importer.factory.FileImporterFactory;
import com.tperons.mapper.PersonMapper;
import com.tperons.repository.PersonRepository;

import jakarta.transaction.Transactional;

@Service
public class PersonService {

    private Logger logger = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository repository;
    private final PersonMapper mapper;
    private final FileImporterFactory importerFactory;
    private final FileExporterFactory exporterFactory;

    public PersonService(PersonRepository repository, PersonMapper mapper, FileImporterFactory importerFactory, FileExporterFactory exporterFactory) {
        this.repository = repository;
        this.mapper = mapper;
        this.importerFactory = importerFactory;
        this.exporterFactory = exporterFactory;
    }

    public Page<PersonDTO> findAll(Pageable pageable) {
        logger.info("Finding all People!");
        Page<Person> personPage = repository.findAll(pageable);
        Page<PersonDTO> dtoPage = personPage.map(p -> mapper.toDTO(p));
        dtoPage.forEach(p -> addHateoasLinks(p));
        return dtoPage;
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var dto = mapper.toDTO(entity);
        addHateoasLinks(dto);
        return dto;
    }

    public Page<PersonDTO> findByName(String firstName, Pageable pageable) {
        logger.info("Finding People by First Name!");
        Page<Person> personPage = repository.findByName(firstName, pageable);
        Page<PersonDTO> dtoPage = personPage.map(p -> mapper.toDTO(p));
        dtoPage.forEach(p -> addHateoasLinks(p));
        return dtoPage;
    }

    public Resource exportPerson(Long id, String acceptHeader) {
        logger.info("Exporting Data of one Person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var dto = mapper.toDTO(entity);
        try {
            PersonExporter exporter = exporterFactory.getExporter(acceptHeader);
            return exporter.exportPerson(dto);
        } catch (Exception e) {
            logger.error("Error on file export");
            throw new RuntimeException("File generate error.", e);
        }
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Finding all People!");
        Page<Person> personPage = repository.findAll(pageable);
        List<PersonDTO> dtoList = personPage.getContent().stream().map(p -> mapper.toDTO(p)).toList();
        try {
            PersonExporter exporter = exporterFactory.getExporter(acceptHeader);
            return exporter.exportPeople(dtoList);
        } catch (Exception e) {
            logger.error("Error on file export");
            throw new RuntimeException("File generate error.", e);
        }
    }

    public PersonDTO create(PersonDTO obj) {
        if (obj == null)
            throw new RequiredObjectIsNullException();
        logger.info("Creating one Person!");
        var entity = mapper.toEntity(obj);
        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    public List<PersonDTO> massCreation(MultipartFile file) {
        logger.info("Importing people from file!");
        if (file.isEmpty())
            throw new BadRequestException("Please set a valid file!");
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cannot be null!"));
            FileImporter importer = this.importerFactory.getImporter(fileName);
            List<PersonDTO> importedDtos = importer.importFile(inputStream);
            List<Person> entitiesToSave = mapper.toEntityList(importedDtos);
            List<Person> savedEntities = repository.saveAll(entitiesToSave);
            List<PersonDTO> savedDtos = mapper.toDTOList(savedEntities);
            savedDtos.forEach(dto -> addHateoasLinks(dto));
            return savedDtos;
        } catch (Exception e) {
            throw new BadRequestException("Failed to process the uploaded file!");
        }
    }

    public PersonDTO update(Long id, PersonDTO obj) {
        if (obj == null)
            throw new RequiredObjectIsNullException();
        logger.info("Updating one Person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setFirstName(obj.getFirstName());
        entity.setLastName(obj.getLastName());
        entity.setAddress(obj.getAddress());
        entity.setGender(obj.getGender());
        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("Disabling one Person");
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for the ID!"));
        repository.disablePerson(id);
        var entity = repository.findById(id).get();
        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    private static void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findAll(null, null, null, null)).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findByName(null, null, null, null, null)).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }

}
