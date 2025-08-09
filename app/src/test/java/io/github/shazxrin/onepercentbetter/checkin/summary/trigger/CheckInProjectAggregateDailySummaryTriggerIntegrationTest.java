package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectAggregateDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectAggregateDailySummaryService;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
public class CheckInProjectAggregateDailySummaryTriggerIntegrationTest {
    @Container
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/dev.compose.yaml"));

    @Autowired
    private CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;

    @Autowired
    private CheckInProjectAggregateDailySummaryTrigger checkInProjectAggregateDailySummaryTrigger;

    @Test
    void testRunScheduledInitAggregateDailySummary_shouldInitializeYearlySummaries() {
        // Given
        checkInProjectAggregateDailySummaryRepository.deleteAll();

        // When
        checkInProjectAggregateDailySummaryTrigger.runScheduledInitAggregateDailySummary();

        // Then
        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<CheckInProjectAggregateDailySummary> summaries = 
                checkInProjectAggregateDailySummaryRepository.findAll();
            
            LocalDate now = LocalDate.now();
            int expectedCount = now.lengthOfYear();
            
            assertEquals(expectedCount, summaries.size());
            
            // Verify first day of year exists
            assertTrue(summaries.stream()
                .anyMatch(s -> s.getDate().equals(now.withDayOfYear(1))));
            
            // Verify last day of year exists
            assertTrue(summaries.stream()
                .anyMatch(s -> s.getDate().equals(now.withDayOfYear(expectedCount))));
            
            // Verify all summaries have initial values
            assertTrue(summaries.stream()
                .allMatch(s -> s.getNoOfCheckIns() == 0 && s.getStreak() == 0));
        });
    }
}