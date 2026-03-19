package com.tperons.file.exporter.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.tperons.exception.BadRequestException;
import com.tperons.file.exporter.MediaTypes;
import com.tperons.file.exporter.contract.PersonExporter;
import com.tperons.file.exporter.impl.CsvExporter;
import com.tperons.file.exporter.impl.PdfExporter;
import com.tperons.file.exporter.impl.XlsxExporter;

@Component
public class FileExporterFactory {

    private static final Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    private final ApplicationContext applicationContext;

    public FileExporterFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public PersonExporter getExporter(String acceptHeader) throws Exception {
        logger.info("Determining file exporter for Accept header: {}", acceptHeader);
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return applicationContext.getBean(XlsxExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return applicationContext.getBean(CsvExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return applicationContext.getBean(PdfExporter.class);
        } else {
            logger.error("Failed to determine exporter. Unsupported media type: {}", acceptHeader);
            throw new BadRequestException("Invalid file format!");
        }
    }

}
