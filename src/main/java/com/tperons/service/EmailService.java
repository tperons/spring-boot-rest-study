package com.tperons.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tperons.config.EmailConfig;
import com.tperons.dto.request.EmailRequestDTO;
import com.tperons.exception.EmailException;
import com.tperons.mail.EmailSender;

@Service
public class EmailService {

    private final EmailSender emailSender;
    private final EmailConfig emailConfigs;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmailService(EmailSender emailSender, EmailConfig emailConfigs) {
        this.emailSender = emailSender;
        this.emailConfigs = emailConfigs;
    }

    public void sendSimpleEmail(EmailRequestDTO emailRequestDTO) {
        emailSender.send(emailConfigs, emailRequestDTO.getTo(), emailRequestDTO.getSubject(), emailRequestDTO.getBody(),
                null);
    }

    public void sendEmailWithAttachment(String emailRequestJson, MultipartFile attachment) {
        File tempFile = null;
        try {
            EmailRequestDTO emailRequestDTO = objectMapper.readValue(emailRequestJson, EmailRequestDTO.class);
            tempFile = File.createTempFile("attachment", attachment.getOriginalFilename());
            attachment.transferTo(tempFile);

            emailSender.send(
                    emailConfigs,
                    emailRequestDTO.getTo(),
                    emailRequestDTO.getSubject(),
                    emailRequestDTO.getBody(),
                    tempFile);
        } catch (JsonProcessingException e) {
            throw new EmailException("Error parsing e-mail request.", e);
        } catch (IOException e) {
            throw new EmailException("Error processing the attachment.", e);
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

}
