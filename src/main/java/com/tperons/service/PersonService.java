package com.tperons.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tperons.data.dto.PersonDTO;
import com.tperons.entity.Person;
import com.tperons.exception.ResourceNotFoundException;
import com.tperons.mapper.ObjectMapper;
import com.tperons.repository.PersonRepository;

@Service
public class PersonService {

    private Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonRepository repository;

    public List<PersonDTO> findAll() {
        logger.info("Finding all People!");
        return ObjectMapper.parseListObjects(repository.findAll(), PersonDTO.class);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return ObjectMapper.parseObject(entity, PersonDTO.class);
    }

    public PersonDTO create(PersonDTO obj) {
        logger.info("Creating one Person!");
        var entity = ObjectMapper.parseObject(obj, Person.class);
        return ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
    }

    public PersonDTO update(Long id, PersonDTO obj) {
        logger.info("Updating one Person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setFirstName(obj.getFirstName());
        entity.setLastName(obj.getLastName());
        entity.setAddress(obj.getAddress());
        entity.setGender(obj.getGender());
        return ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);

    }

}
