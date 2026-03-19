package com.tperons.file.importer.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.tperons.dto.PersonDTO;
import com.tperons.file.importer.contract.FileImporter;

@Component
public class CsvImporter implements FileImporter {

    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        CSVFormat format = CSVFormat.Builder
                .create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true).get();

        Iterable<CSVRecord> records = format.parse(new InputStreamReader(inputStream));

        return parseRecordsToPersonDTOs(records);
    }

    private List<PersonDTO> parseRecordsToPersonDTOs(Iterable<CSVRecord> records) {
        List<PersonDTO> people = new ArrayList<>();

        for (CSVRecord record : records) {
            PersonDTO person = new PersonDTO(
                    null,
                    record.get("first_name"),
                    record.get("last_name"),
                    record.get("address"),
                    record.get("gender"),
                    true);
            people.add(person);
        }

        return people;
    }

}
