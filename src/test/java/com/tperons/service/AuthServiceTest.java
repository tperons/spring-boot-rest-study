package com.tperons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tperons.dto.security.AccountCredentialsDTO;
import com.tperons.dto.security.TokenDTO;
import com.tperons.entity.User;
import com.tperons.exception.RequiredObjectIsNullException;
import com.tperons.mocks.AuthFactory;
import com.tperons.repository.UserRepository;
import com.tperons.security.jwt.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Tests")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return a valid TokenDTO when login credentials are correct")
    void should_returnTokenDTO_when_signInWithValidCredentials() {
        AccountCredentialsDTO credentials = AuthFactory.createMockCredentials();
        User mockUser = AuthFactory.createMockUserEntity();
        TokenDTO mockToken = AuthFactory.createMockToken();

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(mockUser));
        when(jwtTokenProvider.createAccessToken(eq("admin"), any())).thenReturn(mockToken);

        ResponseEntity<TokenDTO> response = authService.signIn(credentials);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockToken, response.getBody());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUserName("admin");
        verify(jwtTokenProvider, times(1)).createAccessToken(eq("admin"), any());
    }

    @Test
    @DisplayName("Should return a DTO with a null password when creating a new valid user")
    void should_returnCredentialsWithoutPassword_when_createWithValidData() {
        AccountCredentialsDTO inputCredentials = AuthFactory.createMockCredentials();
        User savedUser = AuthFactory.createMockUserEntity();

        when(passwordEncoder.encode("password-0")).thenReturn("encoded-password-0");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AccountCredentialsDTO response = authService.create(inputCredentials);

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals("Administrator", response.getFullName());
        assertNull(response.getPassword());

        verify(passwordEncoder, times(1)).encode("password-0");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw RequiredObjectIsNullException when trying to create a user with null input")
    void should_throwRequiredObjectIsNullException_when_createWithNullInput() {
        assertThrows(RequiredObjectIsNullException.class, () -> authService.create(null));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
