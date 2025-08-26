package io.github.shazxrin.onepercentbetter.checkin.onboard.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.daily.service.CheckInProjectDailySummaryService;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service.CheckInProjectMonthlySummaryService;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.service.CheckInProjectWeeklySummaryService;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectYearlySummaryService;
import io.github.shazxrin.onepercentbetter.project.event.ProjectAddedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectOnboardTriggerTest {
    @Mock
    private CheckInProjectService checkInProjectService;

    @Mock
    private CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    @Mock
    private CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService;

    @Mock
    private CheckInProjectMonthlySummaryService checkInProjectMonthlySummaryService;

    @Mock
    private CheckInProjectYearlySummaryService checkInProjectYearlySummaryService;

    @InjectMocks
    private CheckInProjectOnboardTrigger checkInProjectOnboardTrigger;

    @Test
    void testRunOnboardProjectCheckIns_shouldInitSummariesAndCheckInProject() {
        // Given
        long projectId = 123L;
        ProjectAddedEvent event = new ProjectAddedEvent(this, projectId);
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfYear = now.withDayOfYear(1);

        // When
        checkInProjectOnboardTrigger.runOnboardProjectCheckIns(event);

        // Then
        verify(checkInProjectDailySummaryService, times(1)).initSummary(eq(projectId));
        verify(checkInProjectWeeklySummaryService, times(1)).initSummary(eq(projectId));
        verify(checkInProjectMonthlySummaryService, times(1)).initSummary(eq(projectId));
        verify(checkInProjectYearlySummaryService, times(1)).initSummary(eq(projectId));
        verify(checkInProjectService, times(1)).checkInInterval(eq(projectId), eq(firstDayOfYear), eq(now));
    }
}
