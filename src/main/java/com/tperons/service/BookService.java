package com.tperons.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tperons.controller.BookController;
import com.tperons.dto.BookDTO;
import com.tperons.entity.Book;
import com.tperons.exception.RequiredObjectIsNullException;
import com.tperons.exception.ResourceNotFoundException;
import com.tperons.mapper.BookMapper;
import com.tperons.repository.BookRepository;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public Page<BookDTO> findAll(Pageable pageable) {
        logger.info("Finding all books.");
        Page<Book> bookPage = bookRepository.findAll(pageable);
        Page<BookDTO> dtoPage = bookPage.map(b -> bookMapper.toDTO(b));
        dtoPage.forEach(b -> addHateoasLinks(b));
        return dtoPage;
    }

    public BookDTO findById(Long id) {
        logger.info("Finding one book.");
        var entity = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var dto = bookMapper.toDTO(entity);
        addHateoasLinks(dto);
        return dto;
    }

    public Page<BookDTO> findByTitle(String title, Pageable pageable) {
        logger.info("Finding books by title.");
        Page<Book> bookPage = bookRepository.findByTitle(title, pageable);
        Page<BookDTO> dtoPage = bookPage.map(p -> bookMapper.toDTO(p));
        dtoPage.forEach(p -> addHateoasLinks(p));
        return dtoPage;
    }

    public BookDTO create(BookDTO obj) {
        if (obj == null) {
            throw new RequiredObjectIsNullException();
        }
        logger.info("Creating one book.");
        var entity = bookMapper.toEntity(obj);
        var dto = bookMapper.toDTO(bookRepository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO update(Long id, BookDTO obj) {
        if (obj == null) {
            throw new RequiredObjectIsNullException();
        }
        logger.info("Updating one book.");
        Book entity = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setTitle(obj.getTitle());
        entity.setAuthor(obj.getAuthor());
        entity.setLaunchDate(obj.getLaunchDate());
        entity.setPrice(obj.getPrice());
        var dto = bookMapper.toDTO(bookRepository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting one book.");
        Book entity = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        bookRepository.delete(entity);
    }

    private static void addHateoasLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findAll(null, null, null, null)).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findByTitle(null, null, null, null, null)).withRel("findByTitle").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }

}
