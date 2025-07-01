package io.github.shazxrin.onepercentbetter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class ApplicationTest {

    @Test
    void contextLoads() {
    }

}
