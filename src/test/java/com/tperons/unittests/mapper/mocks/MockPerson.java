package com.tperons.unittests.mapper.mocks;

import java.util.ArrayList;
import java.util.List;

import com.tperons.dto.PersonDTO;
import com.tperons.entity.Person;

public class MockPerson {

    public Person mockEntity() {
        return mockEntity(0);
    }

    public PersonDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Person> mockEntityList() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            people.add(mockEntity(i));
        }
        return people;
    }

    public List<PersonDTO> mockDTOList() {
        List<PersonDTO> people = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            people.add(mockDTO(i));
        }
        return people;
    }

    public Person mockEntity(Integer number) {
        Person person = new Person(
                number.longValue(),
                "First Name Test" + number,
                "Last Name Test" + number,
                "Address Test" + number,
                ((number % 2) == 0) ? "Male" : "Female",
                true,
                null,
                null);
        return person;
    }

    public PersonDTO mockDTO(Integer number) {
        PersonDTO person = new PersonDTO(
                number.longValue(),
                "First Name Test" + number,
                "Last Name Test" + number,
                "Address Test" + number,
                ((number % 2) == 0) ? "Male" : "Female",
                true);
        return person;
    }

}
