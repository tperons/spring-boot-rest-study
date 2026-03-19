package com.tperons.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tperons.controller.docs.AuthControllerDocs;
import com.tperons.dto.security.AccountCredentialsDTO;
import com.tperons.service.AuthService;

@RestController
@RequestMapping(value = "/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @PostMapping(value = "/createUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountCredentialsDTO> create(@RequestBody AccountCredentialsDTO credentials) {
        AccountCredentialsDTO savedObj = authService.create(credentials);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedObj);
    }

    @Override
    @PostMapping(value = "/signin")
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentialsDTO) {
        if (credentialsDTO == null || StringUtils.isBlank(credentialsDTO.getPassword()) || StringUtils.isBlank(credentialsDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        var token = authService.signIn(credentialsDTO);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        return ResponseEntity.ok().body(token);
    }

    @Override
    @PutMapping(value = "/refresh")
    public ResponseEntity<?> refreshtoken(@RequestHeader(value = "Authorization") String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        var token = authService.refreshToken(refreshToken);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        return ResponseEntity.ok().body(token);
    }

}
