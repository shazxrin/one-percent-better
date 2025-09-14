package io.github.shazxrin.onepercentbetter;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class ApplicationTest {
    @Container
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/svc.compose.yaml"))
        .withExposedService("postgres", 5432)
        .withExposedService("rabbitmq", 5672);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "spring.datasource.url",
            () -> String.format(
                "jdbc:postgresql://%s:%d/one-percent-better",
                environment.getServiceHost("postgres", 5432),
                environment.getServicePort("postgres", 5432)
            )
        );
        registry.add("spring.datasource.username", () -> "user");
        registry.add("spring.datasource.password", () -> "password");

        registry.add("spring.rabbitmq.host", () -> environment.getServiceHost("rabbitmq", 5672));
        registry.add("spring.rabbitmq.port", () -> environment.getServicePort("rabbitmq", 5672));
        registry.add("spring.rabbitmq.username", () -> "user");
        registry.add("spring.rabbitmq.password", () -> "password");
    }

    @Test
    void contextLoads() {
    }
}
