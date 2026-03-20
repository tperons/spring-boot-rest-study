package com.tperons.mocks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.tperons.dto.security.AccountCredentialsDTO;
import com.tperons.dto.security.TokenDTO;
import com.tperons.entity.User;

public class AuthFactory {

    private static final Instant BASE_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final long TOKEN_EXPIRATION_HOURS = 1L;
    private static final List<String> USERNAMES = List.of(
            "admin", "john.doe", "jane.doe", "moderator", "auditor");
    private static final List<String> FULL_NAMES = List.of(
            "Administrator", "John Doe", "Jane Doe", "Moderator", "Auditor");

    private AuthFactory() {
    }

    public static AccountCredentialsDTO createMockCredentials() {
        return createMockCredentials(0);
    }

    public static AccountCredentialsDTO createMockCredentials(int number) {
        int index = Math.abs(number) % USERNAMES.size();
        return new AccountCredentialsDTO(
                USERNAMES.get(index),
                "password-" + number,
                FULL_NAMES.get(index));
    }

    public static User createMockUserEntity() {
        return createMockUserEntity(0);
    }

    public static User createMockUserEntity(int number) {
        int index = Math.abs(number) % USERNAMES.size();

        User user = new User(
                USERNAMES.get(index),
                "encoded-password-" + number,
                FULL_NAMES.get(index));

        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        return user;
    }

    public static TokenDTO createMockToken() {
        return createMockToken(0);
    }

    public static TokenDTO createMockToken(int number) {
        int index = Math.abs(number) % USERNAMES.size();

        Instant creation = BASE_INSTANT.plus(number, ChronoUnit.MINUTES);
        Instant expiration = creation.plus(TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS);

        return new TokenDTO(
                USERNAMES.get(index),
                true,
                creation,
                expiration,
                "fake-jwt-token-" + number,
                "fake-refresh-token-" + number);
    }
}
