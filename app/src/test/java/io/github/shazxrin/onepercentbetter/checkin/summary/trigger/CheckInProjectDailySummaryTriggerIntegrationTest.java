package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectDailySummaryService;
import io.github.shazxrin.onepercentbetter.github.service.GitHubService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    private GitHubService gitHubService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CheckInProjectDailySummaryService checkInProjectDailySummaryService;
    @Autowired private CheckInProjectRepository checkInProjectRepository;

    @Test
    void testRunAddCheckInToSummary_whenConcurrentEvents_shouldUpdateDailySummaryCorrectly() {
        // Given
        var project = projectRepository.save(new Project("user/repo"));

        checkInProjectDailySummaryService.initSummary(project.getId());

        var checkInProject1 = checkInProjectRepository.save(
            new CheckInProject(LocalDate.now(), "a1", "feat", "message", project)
        );
        var checkInProject2 = checkInProjectRepository.save(
            new CheckInProject(LocalDate.now(), "a2", "feat", "message", project)
        );
        var checkInProject3 = checkInProjectRepository.save(
            new CheckInProject(LocalDate.now(), "a3", "feat", "message", project)
        );
        var checkInProject4 = checkInProjectRepository.save(
            new CheckInProject(LocalDate.now(), "a4", "feat", "message", project)
        );


        // When
        CheckInProjectAddedEvent event1 = new CheckInProjectAddedEvent(
            this,
            project.getId(),
            checkInProject1.getId(),
            LocalDate.now()
        );
        CheckInProjectAddedEvent event2 = new CheckInProjectAddedEvent(
            this,
            project.getId(),
            checkInProject2.getId(),
            LocalDate.now()
        );
        CheckInProjectAddedEvent event3 = new CheckInProjectAddedEvent(
            this,
            project.getId(),
            checkInProject3.getId(),
            LocalDate.now()
        );
        CheckInProjectAddedEvent event4 = new CheckInProjectAddedEvent(
            this,
            project.getId(),
            checkInProject4.getId(),
            LocalDate.now()
        );

        // Publish events 4 times
        applicationEventPublisher.publishEvent(event1);
        applicationEventPublisher.publishEvent(event2);
        applicationEventPublisher.publishEvent(event3);
        applicationEventPublisher.publishEvent(event4);

        // Then
        await().atMost(10, SECONDS).untilAsserted(() -> {
            Optional<CheckInProjectDailySummary> updatedSummaryOpt =
                checkInProjectDailySummaryRepository.findByProjectIdAndDate(project.getId(), LocalDate.now());

            assertTrue(updatedSummaryOpt.isPresent());
            CheckInProjectDailySummary updatedSummary = updatedSummaryOpt.get();
            assertEquals(4, updatedSummary.getNoOfCheckIns());
            assertEquals(1, updatedSummary.getStreak());
        });
    }
}
