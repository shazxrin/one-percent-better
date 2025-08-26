package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectYearlySummaryService;
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
public class CheckInProjectYearlySummaryTriggerTest {
    @Mock
    private CheckInProjectYearlySummaryService checkInProjectYearlySummaryService;

    @InjectMocks
    private CheckInProjectYearlySummaryTrigger checkInProjectYearlySummaryTrigger;

    @Test
    void testRunAddCheckInToSummary_shouldCallAddCheckInToSummaryOnService() {
        // Given
        long projectId = 1L;
        long checkInProjectId = 2L;
        LocalDate date = LocalDate.of(2025, 8, 15);
        CheckInProjectAddedEvent event = new CheckInProjectAddedEvent(this, projectId, checkInProjectId, date);

        // When
        checkInProjectYearlySummaryTrigger.runAddCheckInToSummary(event);

        // Then
        verify(checkInProjectYearlySummaryService, times(1)).addCheckInToSummary(eq(projectId), eq(checkInProjectId));
    }

    @Test
    void testRunScheduledInitYearlySummaries_shouldCallInitSummariesOnService() {
        // When
        checkInProjectYearlySummaryTrigger.runScheduledInitYearlySummaries();
        
        // Then
        verify(checkInProjectYearlySummaryService, times(1)).initSummaries();
    }
}