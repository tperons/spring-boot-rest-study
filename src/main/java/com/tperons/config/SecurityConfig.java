package com.tperons.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tperons.security.jwt.JwtTokenFilter;
import com.tperons.security.jwt.JwtTokenProvider;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${security.argon2.salt-length:16}")
    private int saltLength;

    @Value("${security.argon2.hash-length:32}")
    private int hashLength;

    @Value("${security.argon2.parallelism:1}")
    private int parallelism;

    @Value("${security.argon2.memory:65536}")
    private int memory;

    @Value("${security.argon2.iterations:3}")
    private int iterations;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        PasswordEncoder argon2Encoder = new Argon2PasswordEncoder(
            saltLength, hashLength, parallelism, memory, iterations);

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("argon2", argon2Encoder);

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("argon2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(argon2Encoder);

        return passwordEncoder;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/auth/signin", "/auth/refresh", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/auth/createUser").hasAuthority("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/users").denyAll())
                .cors(cors -> {
                })

                .build();
    }

}
