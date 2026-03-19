package com.tperons.file.importer.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.tperons.exception.BadRequestException;
import com.tperons.file.importer.contract.FileImporter;
import com.tperons.file.importer.impl.CsvImporter;
import com.tperons.file.importer.impl.XlsxImporter;

@Component
public class FileImporterFactory {

    private static final Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);

    private final ApplicationContext applicationContext;

    public FileImporterFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public FileImporter getImporter(String fileName) throws Exception {
        logger.info("Determining file importer for file: {}", fileName);
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".xlsx")) {
            return applicationContext.getBean(XlsxImporter.class);
        } else if (lowerName.endsWith(".csv")) {
            return applicationContext.getBean(CsvImporter.class);
        } else {
            logger.error("Failed to determine importer. Unsupported file extension for: {}", fileName);
            throw new BadRequestException("Invalid file format!");
        }
    }

}
