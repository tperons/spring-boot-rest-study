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
        License license = new License()
                .name("MIT")
                .url("https://github.com/tperons");

        Info info = new Info()
                .title("RESTful API with Java, Spring Boot, Docker, and Kubernetes")
                .version("v1")
                .description("A fully featured RESTful API built from scratch using Java and Spring Boot. " +
                        "This project showcases clean architecture, advanced exception handling, pagination, " +
                        "file export capabilities, and modern deployment strategies using Docker and Kubernetes.")
                .license(license);

        return new OpenAPI()
                .info(info);
    }

}
