package com.tperons.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tperons.controller.docs.EmailControllerDocs;
import com.tperons.dto.request.EmailRequestDTO;
import com.tperons.service.EmailService;

@RestController
@RequestMapping(value = "/api/v1/email")
public class EmailController implements EmailControllerDocs {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        emailService.sendSimpleEmail(emailRequestDTO);

        return new ResponseEntity<>("Email sent successfully.", HttpStatus.OK);
    }

    @Override
    @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequestJson,
            @RequestParam("attachment") MultipartFile attachment) {
        emailService.sendEmailWithAttachment(emailRequestJson, attachment);

        return new ResponseEntity<>("Email with attachment sent successfully.", HttpStatus.OK);
    }

}
