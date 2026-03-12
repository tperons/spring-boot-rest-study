package com.tperons.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tperons.controller.docs.PersonControllerDocs;
import com.tperons.dto.PersonDTO;
import com.tperons.file.exporter.MediaTypes;
import com.tperons.service.PersonService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/person")
public class PersonController implements PersonControllerDocs {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            PagedResourcesAssembler<PersonDTO> assembler) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        Page<PersonDTO> peoplePage = service.findAll(pageable);
        return ResponseEntity.ok().body(assembler.toModel(peoplePage));
    }

    @Override
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTO> findById(@PathVariable("id") Long id) {
        PersonDTO obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @Override
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            PagedResourcesAssembler<PersonDTO> assembler) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        Page<PersonDTO> peoplePage = service.findByName(firstName, pageable);
        PagedModel<EntityModel<PersonDTO>> pagedModel = assembler.toModel(peoplePage);
        return ResponseEntity.ok().body(pagedModel);
    }

    @Override
    @GetMapping(value = "/export/{id}", produces = { MediaTypes.APPLICATION_PDF_VALUE })
    public ResponseEntity<Resource> exportPdf(@PathVariable("id") Long id, HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        Resource fileResource = service.exportPerson(id, acceptHeader);
        String contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=person.pdf")
                .body(fileResource);
    }

    @Override
    @GetMapping(value = "/export", produces = { MediaTypes.APPLICATION_CSV_VALUE, MediaTypes.APPLICATION_PDF_VALUE, MediaTypes.APPLICATION_XLSX_VALUE })
    public ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        Resource fileResource = service.exportPage(pageable, acceptHeader);
        Map<String, String> extensionMap = Map.of(MediaTypes.APPLICATION_CSV_VALUE, ".csv", MediaTypes.APPLICATION_PDF_VALUE, ".pdf", MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx");
        String fileExtension = extensionMap.getOrDefault(acceptHeader, "");
        String contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
        String fileName = "people_exported_" + page + fileExtension;
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileResource);
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTO> create(@RequestBody PersonDTO obj) {
        PersonDTO savedObj = service.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedObj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(savedObj);
    }

    @Override
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonDTO>> massCreation(@RequestParam("file") MultipartFile file) {
        List<PersonDTO> dtos = service.massCreation(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
    }

    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTO> update(@PathVariable("id") Long id, @RequestBody PersonDTO obj) {
        PersonDTO updatedObj = service.update(id, obj);
        return ResponseEntity.ok().body(updatedObj);
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping(value = "/{id}/disable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTO> disablePerson(@PathVariable("id") Long id) {
        PersonDTO obj = service.disablePerson(id);
        return ResponseEntity.ok().body(obj);
    }

}
