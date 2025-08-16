package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.github.service.GitHubService;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.repository.ProjectRepository;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/dev.compose.yaml"))
        .withExposedService("postgres", 5432)
        .withExposedService("rabbitmq", 5672);

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
    void testAddCheckInToSummary_shouldAddSummaryForProjectAndDate() {
        // Given
        var project = projectRepository.save(new Project("user/repo"));

        var checkInProject = checkInProjectRepository.save(
            new CheckInProject(
                LocalDateTime.now(),
                "abc123",
                "feat",
                "message",
                project
            )
        );

        checkInProjectDailySummaryService.initSummary(checkInProject.getId());

        // When
        checkInProjectDailySummaryService.addCheckInToSummary(project.getId(), checkInProject.getId());

        // Then
        var summaryOpt = checkInProjectDailySummaryRepository.findByProjectIdAndDate(project.getId(), LocalDate.now());
        assertTrue(summaryOpt.isPresent());
        var summary = summaryOpt.get();
        assertEquals(1, summary.getNoOfCheckIns());
        assertEquals(1, summary.getStreak());
        assertEquals(1, summary.getTypeDistribution().get("feat"));
    }
}
