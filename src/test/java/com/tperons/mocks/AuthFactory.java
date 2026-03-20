package com.tperons.mocks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.tperons.dto.security.AccountCredentialsDTO;
import com.tperons.dto.security.TokenDTO;
import com.tperons.entity.User;

public class AuthFactory {

    public AccountCredentialsDTO mockCredentials() {
        return new AccountCredentialsDTO("admin", "admin123", "Administrator");
    }

    public User mockUserEntity() {
        User user = new User("admin", "encoded-password", "Administrator");

        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        return user;
    }

    public TokenDTO mockToken() {
        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);

        return new TokenDTO("admin", true, now, expiration, "fake-jwt-token", "fake-refresh-token");
    }

}
