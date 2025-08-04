package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectAggregateDailySummaryService;
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
public class CheckInProjectAggregateDailySummaryTriggerTest {
    @Mock
    private CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;

    @InjectMocks
    private CheckInProjectAggregateDailySummaryTrigger checkInProjectAggregateDailySummaryTrigger;

    @Test
    void runScheduledCalculateDailySummary_shouldInitDailySummariesForToday() {
        // When
        checkInProjectAggregateDailySummaryTrigger.runScheduledInitAggregateDailySummary();
        
        // Then
        verify(checkInProjectAggregateDailySummaryService, times(1)).initAggregateSummary(eq(LocalDate.now()));
    }
}
