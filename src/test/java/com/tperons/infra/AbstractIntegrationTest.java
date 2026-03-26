package com.tperons.infra;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.postgresql.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public abstract class AbstractIntegrationTest {

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Container
        private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

        private static void startContainers() {
            Startables.deepStart(Stream.of(postgres)).join();
        }

        private static Map<String, Object> createConnectionConfiguration() {
            return Map.of(
                "spring.datasource.url", postgres.getJdbcUrl(),
                "spring.datasource.username", postgres.getUsername(),
                "spring.datasource.password", postgres.getPassword()
            );
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testcontainers = new MapPropertySource("testcontainers", createConnectionConfiguration());
            environment.getPropertySources().addFirst(testcontainers);
        }

    }

}
