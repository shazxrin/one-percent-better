package io.github.shazxrin.onepercentbetter;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class ApplicationTest {
    @Container
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/dev.compose.yaml"));

    @Test
    void contextLoads() {
    }
}
