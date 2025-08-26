package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service.CheckInProjectMonthlySummaryService;
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
public class CheckInProjectMonthlySummaryTriggerTest {
    @Mock
    private CheckInProjectMonthlySummaryService checkInProjectMonthlySummaryService;

    @InjectMocks
    private CheckInProjectMonthlySummaryTrigger checkInProjectMonthlySummaryTrigger;

    @Test
    void testRunAddCheckInToSummary_shouldCallAddCheckInToSummaryOnService() {
        // Given
        long projectId = 1L;
        long checkInProjectId = 2L;
        LocalDate date = LocalDate.of(2025, 8, 15);
        CheckInProjectAddedEvent event = new CheckInProjectAddedEvent(this, projectId, checkInProjectId, date);

        // When
        checkInProjectMonthlySummaryTrigger.runAddCheckInToSummary(event);

        // Then
        verify(checkInProjectMonthlySummaryService, times(1)).addCheckInToSummary(eq(projectId), eq(checkInProjectId));
    }

    @Test
    void testRunScheduledInitMonthlySummaries_shouldCallInitSummariesOnService() {
        // When
        checkInProjectMonthlySummaryTrigger.runScheduledInitMonthlySummaries();
        
        // Then
        verify(checkInProjectMonthlySummaryService, times(1)).initSummaries();
    }
}