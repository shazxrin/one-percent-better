package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.repository.ProjectRepository;
import java.io.File;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
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
public class CheckInProjectDailySummaryTriggerIntegrationTest {
    @Container
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/dev.compose.yaml"));

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testCheckIn_whenNewCommit_shouldUpdateDailySummary() {
        // Given
        // Create a test project
        Project project = new Project();
        project.setName("test-project");
        project = projectRepository.save(project);
        long projectId = project.getId();

        LocalDate today = LocalDate.now();

        // Create an initial summary with 0 check-ins
        CheckInProjectDailySummary initialSummary = new CheckInProjectDailySummary(today, 0, 0, project);
        checkInProjectDailySummaryRepository.save(initialSummary);

        // When
        CheckInProjectAddedEvent event = new CheckInProjectAddedEvent(
            this,
            project.getId(),
            123L,
            today
        );
        applicationEventPublisher.publishEvent(event);

        // Then
        await().atMost(10, SECONDS).untilAsserted(() -> {
            Optional<CheckInProjectDailySummary> updatedSummaryOpt =
                checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, today);

            assertTrue(updatedSummaryOpt.isPresent(), "Daily summary should exist");

            CheckInProjectDailySummary updatedSummary = updatedSummaryOpt.get();
            assertEquals(1, updatedSummary.getNoOfCheckIns());
            assertEquals(1, updatedSummary.getStreak());
        });
    }
}
