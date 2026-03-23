package com.tperons.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tperons.controller.PersonController;
import com.tperons.dto.PersonDTO;
import com.tperons.entity.Person;
import com.tperons.exception.BadRequestException;
import com.tperons.exception.FileStorageException;
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

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final FileImporterFactory fileImporterFactory;
    private final FileExporterFactory fileExporterFactory;

    public PersonService(
            PersonRepository personRepository,
            PersonMapper personMapper,
            FileImporterFactory fileImporterFactory,
            FileExporterFactory fileExporterFactory) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.fileImporterFactory = fileImporterFactory;
        this.fileExporterFactory = fileExporterFactory;
    }

    public Page<PersonDTO> findAll(Pageable pageable) {
        logger.info("Finding all people.");

        Page<Person> personPage = personRepository.findAll(pageable);

        Page<PersonDTO> dtoPage = personPage.map(p -> personMapper.toDTO(p));
        dtoPage.forEach(p -> addHateoasLinks(p));

        return dtoPage;
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one person.");

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var dto = personMapper.toDTO(entity);
        addHateoasLinks(dto);

        return dto;
    }

    public Page<PersonDTO> findByName(String firstName, Pageable pageable) {
        logger.info("Finding people by first name.");
        Page<Person> personPage = personRepository.findByName(firstName, pageable);
        Page<PersonDTO> dtoPage = personPage.map(p -> personMapper.toDTO(p));
        dtoPage.forEach(p -> addHateoasLinks(p));
        return dtoPage;
    }

    public Resource exportPerson(Long id, String acceptHeader) {
        logger.info("Exporting data of one person.");
        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var dto = personMapper.toDTO(entity);
        try {
            PersonExporter exporter = fileExporterFactory.getExporter(acceptHeader);
            return exporter.exportPerson(dto);
        } catch (Exception e) {
            logger.error("Error on file export.");
            throw new FileStorageException("Failed to generate person export file.", e);
        }
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exporting page of people.");
        Page<Person> personPage = personRepository.findAll(pageable);
        List<PersonDTO> dtoList = personPage.getContent().stream().map(p -> personMapper.toDTO(p)).toList();
        try {
            PersonExporter exporter = fileExporterFactory.getExporter(acceptHeader);
            return exporter.exportPeople(dtoList);
        } catch (Exception e) {
            logger.error("Error on file export.");
            throw new FileStorageException("Failed to generate person export file.", e);
        }
    }

    public PersonDTO create(PersonDTO obj) {
        if (obj == null)
            throw new RequiredObjectIsNullException();
        logger.info("Creating one person.");
        var entity = personMapper.toEntity(obj);
        var dto = personMapper.toDTO(personRepository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    public List<PersonDTO> massCreation(MultipartFile file) {
        logger.info("Importing people from file.");
        if (file.isEmpty()) {
            throw new BadRequestException("Please set a valid file!");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = Optional
                    .ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cannot be null!"));
            FileImporter importer = this.fileImporterFactory.getImporter(fileName);

            List<PersonDTO> importedDtos = importer.importFile(inputStream);
            List<Person> entitiesToSave = personMapper.toEntityList(importedDtos);

            List<Person> savedEntities = personRepository.saveAll(entitiesToSave);

            List<PersonDTO> savedDtos = personMapper.toDTOList(savedEntities);
            savedDtos.forEach(dto -> addHateoasLinks(dto));

            return savedDtos;
        } catch (BadRequestException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error during mass creation.", e);
        } catch (Exception e) {
            throw new BadRequestException("Failed to process the uploaded file!", e);
        }
    }

    public PersonDTO update(Long id, PersonDTO obj) {
        if (obj == null)
            throw new RequiredObjectIsNullException();
        logger.info("Updating one person.");

        Person entity = personRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(obj.getFirstName());
        entity.setLastName(obj.getLastName());
        entity.setAddress(obj.getAddress());
        entity.setGender(obj.getGender());

        var dto = personMapper.toDTO(personRepository.save(entity));
        addHateoasLinks(dto);

        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting one person.");

        Person entity = personRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        personRepository.delete(entity);
    }

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("Disabling one person.");
        personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        personRepository.disablePerson(id);

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var dto = personMapper.toDTO(entity);
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
