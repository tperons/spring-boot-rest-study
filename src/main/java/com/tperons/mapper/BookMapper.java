package com.tperons.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.tperons.dto.BookDTO;
import com.tperons.entity.Book;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    BookDTO toDTO(Book book);

    Book toEntity(BookDTO dto);

    List<BookDTO> toDTOList(List<Book> books);

    List<Book> toEntityList(List<BookDTO> dtos);

}
