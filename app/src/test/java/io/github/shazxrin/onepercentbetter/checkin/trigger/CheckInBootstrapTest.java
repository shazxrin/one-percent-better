package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.configuration.CheckInProperties;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
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
    private CheckInProperties checkInProperties;

    @Mock
    private CheckInService checkInService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private CheckInBootstrap checkInBootstrap;

    private static final LocalDate TODAY = LocalDate.now();
    private static final String BOOTSTRAP_DATE = "2023-01-01";
    private static final LocalDate PARSED_BOOTSTRAP_DATE = LocalDate.parse(BOOTSTRAP_DATE);

    @Test
    void testCheckInBootstrap_withValidProjects_shouldAddProjectsAndCheckIn() {
        // Given
        String validProject1 = "owner1/project1";
        String validProject2 = "owner2/project2";

        CheckInProperties.Bootstrap bootstrap = new CheckInProperties.Bootstrap();
        bootstrap.setDate(BOOTSTRAP_DATE);
        bootstrap.setProjects(Arrays.asList(validProject1, validProject2));
        when(checkInProperties.getBootstrap()).thenReturn(bootstrap);

        // When
        checkInBootstrap.checkInBootstrap();

        // Then
        verify(projectService).addProject("owner1/project1");
        verify(projectService).addProject("owner2/project2");
        verify(checkInService).checkInInterval(PARSED_BOOTSTRAP_DATE, TODAY);
    }

    @Test
    void testCheckInBootstrap_checksCorrectDateInterval() {
        // Given
        CheckInProperties.Bootstrap bootstrap = new CheckInProperties.Bootstrap();
        bootstrap.setDate(BOOTSTRAP_DATE);
        bootstrap.setProjects(Collections.emptyList());
        when(checkInProperties.getBootstrap()).thenReturn(bootstrap);
        
        // When
        checkInBootstrap.checkInBootstrap();

        // Then
        verify(checkInService).checkInInterval(PARSED_BOOTSTRAP_DATE, TODAY);
    }
}
