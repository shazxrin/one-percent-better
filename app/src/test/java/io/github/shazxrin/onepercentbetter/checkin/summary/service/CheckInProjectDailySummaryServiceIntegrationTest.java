package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.github.service.GitHubService;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.repository.ProjectRepository;
import java.io.File;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
public class CheckInProjectDailySummaryServiceIntegrationTest {
    @Container
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/dev.compose.yaml"));

    @MockitoBean
    private GitHubService gitHubService;

    @Autowired
    private CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    @Autowired
    private CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    @Autowired
    private CheckInProjectRepository checkInProjectRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testAddCheckInToSummary_shouldAddSummaryForProjectAndDate() {
        // Given
        var project = projectRepository.save(new Project("user/repo"));

        var checkInProject = checkInProjectRepository.save(
            new CheckInProject(
                LocalDate.now(),
                "abc123",
                "feat",
                "message",
                project
            )
        );

        checkInProjectDailySummaryService.initSummary(checkInProject.getId());

        // When
        checkInProjectDailySummaryService.addCheckInToSummary(project.getId(), checkInProject.getId(), LocalDate.now());

        // Then
        var summaryOpt = checkInProjectDailySummaryRepository.findByProjectIdAndDate(project.getId(), LocalDate.now());
        assertTrue(summaryOpt.isPresent());
        var summary = summaryOpt.get();
        assertEquals(1, summary.getNoOfCheckIns());
        assertEquals(1, summary.getStreak());
        assertEquals(1, summary.getTypeDistribution().get("feat"));
    }
}
