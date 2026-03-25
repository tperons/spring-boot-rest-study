package com.tperons.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tperons.config.properties.Argon2Properties;

@Configuration
@EnableConfigurationProperties(Argon2Properties.class)
public class PasswordEncoderConfig {

    private final Argon2Properties argon2Props;

    public PasswordEncoderConfig(Argon2Properties argon2Props) {
        this.argon2Props = argon2Props;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        PasswordEncoder argon2Encoder = new Argon2PasswordEncoder(
                argon2Props.saltLength(),
                argon2Props.hashLength(),
                argon2Props.parallelism(),
                argon2Props.memory(),
                argon2Props.iterations());

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("argon2", argon2Encoder);

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("argon2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(argon2Encoder);

        return passwordEncoder;
    }

}
