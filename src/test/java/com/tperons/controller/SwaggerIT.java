package com.tperons.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.tperons.infra.AbstractIntegrationTest;

import io.restassured.RestAssured;

public class SwaggerIT extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void should_displaySwaggerUiPage() {
        var content = given().basePath("/swagger-ui/index.html").when().get().then().statusCode(200).extract().body().asString();

        assertTrue(content.contains("Swagger UI"));
    }

    @Test
    void should_returnOpenApiDocsJson() {
        given().basePath("/v3/api-docs").when().get().then().statusCode(200).body("info.title", containsString("RESTful API with Java"));
    }

}
