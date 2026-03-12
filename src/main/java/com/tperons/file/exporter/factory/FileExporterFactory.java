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

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    private final ApplicationContext context;

    public FileExporterFactory(ApplicationContext context) {
        this.context = context;
    }

    public PersonExporter getExporter(String acceptHeader) throws Exception {
        logger.info("Determining file exporter for Accept header: {}", acceptHeader);
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return context.getBean(XlsxExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return context.getBean(CsvExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return context.getBean(PdfExporter.class);
        } else {
            logger.error("Failed to determine exporter. Unsupported media type: {}", acceptHeader);
            throw new BadRequestException("Invalid file format!");
        }
    }

}
