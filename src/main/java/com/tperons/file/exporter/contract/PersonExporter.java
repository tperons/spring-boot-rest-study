package com.tperons.file.exporter.contract;

import java.util.List;

import org.springframework.core.io.Resource;

import com.tperons.dto.PersonDTO;

public interface PersonExporter {

    Resource exportPeople(List<PersonDTO> people) throws Exception;
    Resource exportPerson(PersonDTO person) throws Exception;

}
