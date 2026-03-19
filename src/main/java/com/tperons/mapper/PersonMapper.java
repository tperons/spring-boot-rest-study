package com.tperons.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.tperons.dto.PersonDTO;
import com.tperons.entity.Person;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    PersonDTO toDTO(Person person);

    Person toEntity(PersonDTO dto);

    List<PersonDTO> toDTOList(List<Person> people);

    List<Person> toEntityList(List<PersonDTO> dtos);

}
