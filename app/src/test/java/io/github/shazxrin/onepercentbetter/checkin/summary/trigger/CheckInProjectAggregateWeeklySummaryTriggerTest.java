package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectAggregateWeeklySummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectAggregateWeeklySummaryTriggerTest {
    @Mock
    private CheckInProjectAggregateWeeklySummaryService service;

    @InjectMocks
    private CheckInProjectAggregateWeeklySummaryTrigger trigger;

    @Test
    void runScheduledInitAggregateWeeklySummaries_shouldInitAggregateSummaries() {
        // When
        trigger.runScheduledInitAggregateWeeklySummaries();

        // Then
        verify(service, times(1)).initAggregateSummaries();
    }
}