package com.tperons.mocks;

import java.util.ArrayList;
import java.util.List;

import com.tperons.dto.PersonDTO;
import com.tperons.entity.Person;

public class PersonFactory {

    private static final int DEFAULT_MOCK_LIST_SIZE = 12;

    private PersonFactory() {
    }

    public static Person createMockEntity() {
        return createMockEntity(0);
    }

    public static PersonDTO createMockDTO() {
        return createMockDTO(0);
    }

    public static List<Person> createMockEntityList() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < DEFAULT_MOCK_LIST_SIZE; i++) {
            people.add(createMockEntity(i));
        }
        return people;
    }

    public static List<PersonDTO> createMockDTOList() {
        List<PersonDTO> people = new ArrayList<>();
        for (int i = 0; i < DEFAULT_MOCK_LIST_SIZE; i++) {
            people.add(createMockDTO(i));
        }
        return people;
    }

    public static Person createMockEntity(int i) {
        return new Person(
                (long) i,
                "First Name " + i,
                "Last Name " + i,
                "Address " + i,
                resolveGender(i),
                resolveEnabled(i),
                "Profile Url " + i,
                "Photo Url " + i);
    }

    public static PersonDTO createMockDTO(int i) {
        Person person = createMockEntity(i);
        return new PersonDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getGender(),
                person.getEnabled());
    }

    private static String resolveGender(int i) {
        return i % 2 == 0 ? "Male" : "Female";
    }

    private static Boolean resolveEnabled(int i) {
        return i % 2 == 0;
    }
}
