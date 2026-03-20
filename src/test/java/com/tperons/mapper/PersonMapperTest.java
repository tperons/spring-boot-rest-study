package com.tperons.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tperons.dto.PersonDTO;
import com.tperons.entity.Person;
import com.tperons.mocks.PersonFactory;

@DisplayName("Person Mapper Unit Tests")
public class PersonMapperTest {

    private PersonFactory inputObject;
    private PersonMapper personMapper;

    @BeforeEach
    public void setUp() {
        inputObject = new PersonFactory();
        personMapper = new PersonMapperImpl();
    }

    @Test
    @DisplayName("Should return a valid PersonDTO when parsing a valid Person entity")
    void should_returnPersonDTO_when_parsingValidPersonEntity() {
        Person entity = inputObject.mockEntity();

        PersonDTO output = personMapper.toDTO(entity);

        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("First Name Test0", output.getFirstName());
        assertEquals("Last Name Test0", output.getLastName());
        assertEquals("Address Test0", output.getAddress());
        assertEquals("Male", output.getGender());
    }

    @Test
    @DisplayName("Should return a valid list of PersonDTO when parsing a list of Person entities")
    void should_returnPersonDTOList_when_parsingValidPersonEntityList() {
        List<Person> entities = inputObject.mockEntityList();

        List<PersonDTO> outputPeople = personMapper.toDTOList(entities);

        PersonDTO outputZero = outputPeople.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());

        PersonDTO outputSeven = outputPeople.get(7);
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());

        PersonDTO outputTwelve = outputPeople.get(12);
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
    }

    @Test
    @DisplayName("Should return a valid Person entity when parsing a valid PersonDTO")
    void should_returnPersonEntity_when_parsingValidPersonDTO() {
        PersonDTO dto = inputObject.mockDTO();

        Person output = personMapper.toEntity(dto);

        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("First Name Test0", output.getFirstName());
        assertEquals("Last Name Test0", output.getLastName());
        assertEquals("Address Test0", output.getAddress());
        assertEquals("Male", output.getGender());
    }

    @Test
    @DisplayName("Should return a valid list of Person entities when parsing a list of PersonDTOs")
    void should_returnPersonEntityList_when_parsingValidPersonDTOList() {
        List<PersonDTO> dtos = inputObject.mockDTOList();

        List<Person> outputPeople = personMapper.toEntityList(dtos);

        Person outputZero = outputPeople.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());

        Person outputSeven = outputPeople.get(7);
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());

        Person outputTwelve = outputPeople.get(12);
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
    }
}
