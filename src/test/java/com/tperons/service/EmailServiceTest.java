package com.tperons.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tperons.config.EmailConfig;
import com.tperons.dto.request.EmailRequestDTO;
import com.tperons.exception.EmailException;
import com.tperons.mail.EmailSender;
import com.tperons.mocks.EmailFactory;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private EmailConfig emailConfig;

    @Mock
    private EmailSender emailSender;

    @Mock
    private File file;

    @Mock
    private MultipartFile attachment;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void should_sendSimpleEmail_when_validEmailRequestIsProvided() {
        EmailRequestDTO emailRequest = EmailFactory.createMockEmailDTO();

        emailService.sendSimpleEmail(emailRequest);

        verify(emailSender, times(1)).send(emailConfig, emailRequest.getTo(), emailRequest.getSubject(),
                emailRequest.getBody(), null);

    }

    @Test
    void should_sendEmailWithAttachment_when_validRequestAndAttachmentAreProvided() throws Exception {
        EmailRequestDTO emailRequest = EmailFactory.createMockEmailDTO();
        String emailRequestJson = "any-json";

        when(objectMapper.readValue(emailRequestJson, EmailRequestDTO.class)).thenReturn(emailRequest);
        when(attachment.getOriginalFilename()).thenReturn("attachment.pdf");

        emailService.sendEmailWithAttachment(emailRequestJson, attachment);

        verify(objectMapper, times(1)).readValue(emailRequestJson, EmailRequestDTO.class);
        verify(attachment, times(1)).getOriginalFilename();
        verify(emailSender, times(1)).send(
                eq(emailConfig),
                eq(emailRequest.getTo()),
                eq(emailRequest.getSubject()),
                eq(emailRequest.getBody()),
                any(File.class));
    }

    @Test
    void should_throwEmailException_when_errorParsingEmailRequest() throws Exception {
        String emailRequestJson = "any-json";

        when(objectMapper.readValue(emailRequestJson, EmailRequestDTO.class)).thenThrow(JsonProcessingException.class);

        assertThrows(EmailException.class,
                () -> emailService.sendEmailWithAttachment(emailRequestJson, attachment));

        verify(objectMapper, times(1)).readValue(emailRequestJson, EmailRequestDTO.class);
        verify(emailSender, never()).send(any(), any(), any(), any(), any());
    }

    @Test
    void should_throwEmailException_when_errorProcessingTheAttachment() throws Exception {
        String emailRequestJson = "any-json";
        EmailRequestDTO emailRequest = EmailFactory.createMockEmailDTO();

        doReturn(emailRequest).when(objectMapper).readValue(emailRequestJson, EmailRequestDTO.class);
        when(attachment.getOriginalFilename()).thenReturn("attachment.pdf");
        doThrow(IOException.class).when(attachment).transferTo(any(File.class));

        assertThrows(EmailException.class,
                () -> emailService.sendEmailWithAttachment(emailRequestJson, attachment));

        verify(emailSender, never()).send(any(), any(), any(), any(), any());
    }

}
