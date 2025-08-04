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
    void testRunCalculateDailySummary_shouldCalculateSummaryForProjectAndDate() {
        // Given
        long projectId = 123L;
        LocalDate testDate = LocalDate.of(2025, 7, 30);
        CheckInProjectAddedEvent event = new CheckInProjectAddedEvent(this, projectId, testDate);
        
        // When
        checkInProjectDailySummaryTrigger.runCalculateDailySummary(event);
        
        // Then
        verify(checkInProjectDailySummaryService, times(1)).calculateSummary(eq(projectId), eq(testDate));
    }
    
    @Test
    void runScheduledCalculateDailySummary_shouldInitDailySummariesForAllProjects() {
        // When
        checkInProjectDailySummaryTrigger.runScheduledInitDailySummaries();
        
        // Then
        verify(checkInProjectDailySummaryService, times(1)).initSummaries(eq(LocalDate.now()));
    }
}
