package com.tperons.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tperons.config.FileStorageConfig;
import com.tperons.exception.FileNotFoundException;
import com.tperons.exception.FileStorageException;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService(FileStorageConfig fileStorageConfig) {
        Path path = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();
        this.fileStorageLocation = path;

        try {
            logger.info("Creating directories.");
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            logger.error("Could not create the directory where files will be stored.");
            throw new FileStorageException("Could not create the directory where files will be stored!", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName).normalize();

            if (!targetLocation.startsWith(this.fileStorageLocation)) {
                throw new FileStorageException("Cannot store file outside the upload directory.");
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (FileStorageException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Could not store file {}.", fileName);
            throw new FileStorageException("Could not store file %s. Please try again.".formatted(fileName), e);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                logger.error("File not found: {}.", fileName);
                throw new FileNotFoundException("File not found: %s.".formatted(fileName));
            }
        } catch (Exception e) {
            logger.error("File not found: {}.", fileName);
            throw new FileNotFoundException("File not found: %s.".formatted(fileName), e);
        }
    }

}
