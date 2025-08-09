package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.github.service.GitHubService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
public class CheckInProjectDailySummaryTriggerIntegrationTest {
    @Container
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/dev.compose.yaml"));

    @MockitoBean
    private GitHubService gitHubService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    @Autowired
    private ProjectService projectService;

    @Test
    void testRunAddCheckInToSummary_whenConcurrentEvents_shouldUpdateDailySummaryCorrectly() {
        // Given
        when(gitHubService.getCommitsForRespositoryOnDate(eq("user"), eq("repo"), any()))
            .thenReturn(List.of());

        // Add project and wait for onboarding to finish (i.e. create daily project summaries)
        var projectId = projectService.addProject("user/repo");
        await().atMost(30, SECONDS).untilAsserted(() -> {
            List<CheckInProjectDailySummary> summaries = checkInProjectDailySummaryRepository.findAll();
            assertEquals(LocalDate.now().lengthOfYear(), summaries.size());
        });

        // When
        CheckInProjectAddedEvent event = new CheckInProjectAddedEvent(
            this,
            projectId,
            123L,
            LocalDate.now()
        );

        // Publish events 4 times
        applicationEventPublisher.publishEvent(event);
        applicationEventPublisher.publishEvent(event);
        applicationEventPublisher.publishEvent(event);
        applicationEventPublisher.publishEvent(event);

        // Then
        await().atMost(10, SECONDS).untilAsserted(() -> {
            Optional<CheckInProjectDailySummary> updatedSummaryOpt =
                checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, LocalDate.now());

            assertTrue(updatedSummaryOpt.isPresent());
            CheckInProjectDailySummary updatedSummary = updatedSummaryOpt.get();
            assertEquals(4, updatedSummary.getNoOfCheckIns());
            assertEquals(1, updatedSummary.getStreak());
        });
    }
}
