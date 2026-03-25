package com.tperons.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "security.argon2")
public record Argon2Properties(
        @DefaultValue("16") int saltLength,
        @DefaultValue("32") int hashLength,
        @DefaultValue("1") int parallelism,
        @DefaultValue("65536") int memory,
        @DefaultValue("3") int iterations) {
}
