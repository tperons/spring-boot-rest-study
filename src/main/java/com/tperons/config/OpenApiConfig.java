package com.tperons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                new Info().title("REST API RESTful from 0 with Java, Spring Boot, Docker and Kubernets").version("v1")
                        .description("REST API RESTful from 0 with Java, Spring Boot, Docker and Kubernets")
                        .license(new License().name("MIT").url("https://github.com/tperons")));
    }

}
