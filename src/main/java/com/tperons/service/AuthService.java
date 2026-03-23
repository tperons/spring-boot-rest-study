package com.tperons.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tperons.dto.security.AccountCredentialsDTO;
import com.tperons.dto.security.TokenDTO;
import com.tperons.entity.User;
import com.tperons.exception.ObjectAlreadyExistsException;
import com.tperons.exception.RequiredObjectIsNullException;
import com.tperons.repository.UserRepository;
import com.tperons.security.jwt.JwtTokenProvider;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));

        var user = userRepository.findByUsername(credentials.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("Username " + credentials.getUsername() + " not found!"));

        var tokenResponse = jwtTokenProvider.createAccessToken(credentials.getUsername(), user.getRoles());
        return ResponseEntity.ok(tokenResponse);
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO user) {
        if (user == null)
            throw new RequiredObjectIsNullException();
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ObjectAlreadyExistsException("There is already a user with this username!");
        }
        logger.info("Creating a new user.");
        var entity = new User(user.getUsername(), passwordEncoder.encode(user.getPassword()), user.getFullName());
        var dto = userRepository.save(entity);
        return new AccountCredentialsDTO(dto.getUsername(), null, dto.getFullName());
    }

    public ResponseEntity<TokenDTO> refreshToken(String refreshToken) {
        TokenDTO token = jwtTokenProvider.refreshToken(refreshToken);
        userRepository.findByUsername(token.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
        return ResponseEntity.ok(token);
    }

}
