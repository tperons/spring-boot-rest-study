package com.tperons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.IanaLinkRelations;

import com.tperons.dto.BookDTO;
import com.tperons.entity.Book;
import com.tperons.exception.RequiredObjectIsNullException;
import com.tperons.exception.ResourceNotFoundException;
import com.tperons.mapper.BookMapper;
import com.tperons.mocks.BookFactory;
import com.tperons.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Test
    void should_returnPageOfBookDTO_when_findAllIsCalled() {
        Pageable pageable = PageRequest.of(0, 12);
        List<Book> entityList = BookFactory.createMockEntityList();
        Page<Book> entityPage = new PageImpl<>(entityList, pageable, entityList.size());
        BookDTO dto = BookFactory.createMockDTO(1);

        when(bookRepository.findAll(pageable)).thenReturn(entityPage);
        when(bookMapper.toDTO(any(Book.class))).thenReturn(dto);

        Page<BookDTO> result = bookService.findAll(pageable);

        assertNotNull(result);
        assertEquals(entityList.size(), result.getTotalElements());
        assertTrue(result.getContent().get(0).hasLink(IanaLinkRelations.SELF));

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(entityList.size())).toDTO(any(Book.class));
    }

    @Test
    void should_returnBookDTO_when_findByIdExists() {
        Book entity = BookFactory.createMockEntity(1);
        BookDTO dto = BookFactory.createMockDTO(1);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(bookMapper.toDTO(entity)).thenReturn(dto);

        BookDTO result = bookService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookFactory.createMockDTO(1).getTitle(), result.getTitle());
        assertEquals(dto, result);
        assertTrue(result.hasLink(IanaLinkRelations.SELF));

        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, times(1)).toDTO(entity);
    }

    @Test
    void should_throwResourceNotFoundException_when_findByIdDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findById(1L));

        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, never()).toDTO(any(Book.class));
    }

    @Test
    void should_returnBookDTO_when_createWithValidData() {
        Book entity = BookFactory.createMockEntity(1);
        BookDTO dto = BookFactory.createMockDTO(1);

        when(bookMapper.toEntity(dto)).thenReturn(entity);
        when(bookRepository.save(entity)).thenReturn(entity);
        when(bookMapper.toDTO(entity)).thenReturn(dto);

        BookDTO result = bookService.create(dto);

        assertNotNull(result);
        assertEquals(dto, result);
        assertTrue(result.hasLink(IanaLinkRelations.SELF));

        verify(bookRepository, times(1)).save(entity);
        verify(bookMapper, times(1)).toDTO(entity);
    }

    @Test
    void should_throwRequiredObjectIsNullException_when_createWithNullData() {
        assertThrows(RequiredObjectIsNullException.class, () -> bookService.create(null));

        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).toEntity(any(BookDTO.class));
    }

    @Test
    void should_returnUpdatedBookDTO_when_updateWithValidData() {
        Book entity = BookFactory.createMockEntity(1);
        BookDTO dto = BookFactory.createMockDTO(1);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(bookRepository.save(entity)).thenReturn(entity);
        when(bookMapper.toDTO(entity)).thenReturn(dto);

        BookDTO result = bookService.update(1L, dto);

        assertNotNull(result);
        assertEquals(dto, result);
        assertTrue(result.hasLink(IanaLinkRelations.SELF));

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(entity);
        verify(bookMapper, times(1)).toDTO(entity);
    }

    @Test
    void should_throwRequiredObjectIsNullException_when_updateWithNullData() {
        assertThrows(RequiredObjectIsNullException.class, () -> bookService.update(1L, null));

        verify(bookRepository, never()).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).toEntity(any(BookDTO.class));

    }

    @Test
    void should_throwResourceNotFoundException_when_updateWithNonExistentId() {
        BookDTO dto = BookFactory.createMockDTO(1);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.update(1L, dto));

        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).toDTO(any(Book.class));
    }

    @Test
    void should_deleteBook_when_idExists() {
        Book entity = BookFactory.createMockEntity(1);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));

        bookService.delete(1L);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).delete(entity);
    }

    @Test
    void should_throwResourceNotFoundException_when_deleteWithNonExistentId() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(1L));

        verify(bookRepository, never()).delete(any(Book.class));
    }

}
