package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectAggregateWeeklySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.repository.CheckInProjectAggregateWeeklySummaryRepository;
import java.io.File;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
public class CheckInProjectAggregateWeeklySummaryTriggerIntegrationTest {
    @Container
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/dev.compose.yaml"))
        .withExposedService("postgres", 5432)
        .withExposedService("rabbitmq", 5672);

    @Autowired
    private CheckInProjectAggregateWeeklySummaryRepository repository;

    @Autowired
    private CheckInProjectAggregateWeeklySummaryTrigger trigger;

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
    void testRunScheduledInitAggregateWeeklySummaries_shouldInitializeYearlySummaries() {
        // Given
        repository.deleteAll();

        // When
        trigger.runScheduledInitAggregateWeeklySummaries();

        // Then
        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<CheckInProjectAggregateWeeklySummary> summaries = repository.findAll();

            LocalDate now = LocalDate.now();
            long expectedWeeks = now.range(WeekFields.ISO.weekOfWeekBasedYear()).getMaximum();

            assertTrue(summaries.size() >= expectedWeeks);

            // Verify first week exists
            assertTrue(summaries.stream().anyMatch(s -> s.getWeekNo() == 1));

            // Verify last week exists
            assertTrue(summaries.stream().anyMatch(s -> s.getWeekNo() == expectedWeeks));

            // Verify all summaries have initial values
            assertTrue(summaries.stream().allMatch(s -> s.getNoOfCheckIns() == 0 && s.getStreak() == 0));
        });
    }
}