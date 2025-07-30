package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.configuration.CheckInProjectProperties;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectTriggerTest {
    @Mock
    private CheckInProjectProperties checkInProjectProperties;

    @Mock
    private CheckInProjectService checkInProjectService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private CheckInProjectTrigger checkInProjectTrigger;

    private static final LocalDate TODAY = LocalDate.now();
    private static final String BOOTSTRAP_DATE = "2023-01-01";
    private static final LocalDate PARSED_BOOTSTRAP_DATE = LocalDate.parse(BOOTSTRAP_DATE);

    @Test
    void testRunScheduledCheckInProjectsAll_shouldCallCheckInServiceCheckInToday() {
        // When
        checkInProjectTrigger.runScheduledCheckInProjectsAll();

        // Then
        verify(checkInProjectService, times(1)).checkInAll(eq(LocalDate.now()));
    }

    @Test
    void testRunBootstrapCheckInProjectsAll_withValidProjects_shouldAddProjectsAndCheckIn() {
        // Given
        String validProject1 = "owner1/project1";
        String validProject2 = "owner2/project2";

        CheckInProjectProperties.Bootstrap bootstrap = new CheckInProjectProperties.Bootstrap();
        bootstrap.setDate(BOOTSTRAP_DATE);
        bootstrap.setProjects(Arrays.asList(validProject1, validProject2));
        when(checkInProjectProperties.getBootstrap()).thenReturn(bootstrap);

        // When
        checkInProjectTrigger.runBootstrapCheckInProjectsAll();

        // Then
        verify(projectService).addProject("owner1/project1");
        verify(projectService).addProject("owner2/project2");
        verify(checkInProjectService).checkInAllInterval(PARSED_BOOTSTRAP_DATE, TODAY);
    }

    @Test
    void testRunBootstrapCheckInProjectsAll_checksCorrectDateInterval() {
        // Given
        CheckInProjectProperties.Bootstrap bootstrap = new CheckInProjectProperties.Bootstrap();
        bootstrap.setDate(BOOTSTRAP_DATE);
        bootstrap.setProjects(Collections.emptyList());
        when(checkInProjectProperties.getBootstrap()).thenReturn(bootstrap);

        // When
        checkInProjectTrigger.runBootstrapCheckInProjectsAll();

        // Then
        verify(checkInProjectService).checkInAllInterval(PARSED_BOOTSTRAP_DATE, TODAY);
    }

}
