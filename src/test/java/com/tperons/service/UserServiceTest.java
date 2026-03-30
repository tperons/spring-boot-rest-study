package com.tperons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tperons.entity.User;
import com.tperons.mocks.AuthFactory;
import com.tperons.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void should_returnUserDetails_when_loadUserByUsername() {
        User user = AuthFactory.createMockUserEntity();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails response = userService.loadUserByUsername(user.getUsername());

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void should_throwUsernameNotFoundException_when_usernameDoesNotExist() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonExistentUser"));

        verify(userRepository, times(1)).findByUsername("nonExistentUser");
    }

}
