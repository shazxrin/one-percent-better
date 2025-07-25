package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.configuration.CheckInProjectProperties;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInBootstrapTest {

    @Mock
    private CheckInProjectProperties checkInProperties;

    @Mock
    private CheckInProjectService checkInService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private CheckInProjectBootstrap checkInBootstrap;

    private static final LocalDate TODAY = LocalDate.now();
    private static final String BOOTSTRAP_DATE = "2023-01-01";
    private static final LocalDate PARSED_BOOTSTRAP_DATE = LocalDate.parse(BOOTSTRAP_DATE);

    @Test
    void testCheckInBootstrap_withValidProjects_shouldAddProjectsAndCheckIn() {
        // Given
        String validProject1 = "owner1/project1";
        String validProject2 = "owner2/project2";

        CheckInProjectProperties.Bootstrap bootstrap = new CheckInProjectProperties.Bootstrap();
        bootstrap.setDate(BOOTSTRAP_DATE);
        bootstrap.setProjects(Arrays.asList(validProject1, validProject2));
        when(checkInProperties.getBootstrap()).thenReturn(bootstrap);

        // When
        checkInBootstrap.runBootstrapCheckInProjectsAll();

        // Then
        verify(projectService).addProject("owner1/project1");
        verify(projectService).addProject("owner2/project2");
        verify(checkInService).checkInAllInterval(PARSED_BOOTSTRAP_DATE, TODAY);
    }

    @Test
    void testCheckInBootstrap_checksCorrectDateInterval() {
        // Given
        CheckInProjectProperties.Bootstrap bootstrap = new CheckInProjectProperties.Bootstrap();
        bootstrap.setDate(BOOTSTRAP_DATE);
        bootstrap.setProjects(Collections.emptyList());
        when(checkInProperties.getBootstrap()).thenReturn(bootstrap);
        
        // When
        checkInBootstrap.runBootstrapCheckInProjectsAll();

        // Then
        verify(checkInService).checkInAllInterval(PARSED_BOOTSTRAP_DATE, TODAY);
    }
}
