package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectDailySummaryService;
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
public class CheckInProjectDailySummaryTriggerTest {
    @Mock
    private CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    @InjectMocks
    private CheckInProjectDailySummaryTrigger checkInProjectDailySummaryTrigger;

    @Test
    void testRunAddCheckInToSummary_shouldCalculateSummaryForProjectAndDate() {
        // Given
        long projectId = 123L;
        long checkInProjectId = 456L;
        LocalDate testDate = LocalDate.of(2025, 7, 30);
        CheckInProjectAddedEvent event = new CheckInProjectAddedEvent(this, projectId, checkInProjectId, testDate);
        
        // When
        checkInProjectDailySummaryTrigger.runAddCheckInToSummary(event);
        
        // Then
        verify(checkInProjectDailySummaryService, times(1)).addCheckInToSummary(eq(projectId), eq(checkInProjectId), eq(testDate));
    }

    @Test
    void testRunScheduledInitDailySummaries_shouldInitDailySummariesForAllProjects() {
        // When
        checkInProjectDailySummaryTrigger.runScheduledInitDailySummaries();
        
        // Then
        verify(checkInProjectDailySummaryService, times(1)).initSummaries();
    }
}
