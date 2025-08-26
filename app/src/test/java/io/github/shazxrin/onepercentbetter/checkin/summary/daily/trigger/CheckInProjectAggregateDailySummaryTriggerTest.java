package io.github.shazxrin.onepercentbetter.checkin.summary.daily.trigger;

import io.github.shazxrin.onepercentbetter.checkin.summary.daily.service.CheckInProjectAggregateDailySummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectAggregateDailySummaryTriggerTest {
    @Mock
    private CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;

    @InjectMocks
    private CheckInProjectAggregateDailySummaryTrigger checkInProjectAggregateDailySummaryTrigger;

    @Test
    void runScheduledInitAggregateDailySummary_shouldInitAggregateSummaries() {
        // When
        checkInProjectAggregateDailySummaryTrigger.runScheduledInitAggregateDailySummary();
        
        // Then
        verify(checkInProjectAggregateDailySummaryService, times(1)).initAggregateSummaries();
    }
}
