package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectAggregateYearlySummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CheckInProjectAggregateYearlySummaryTriggerTest {

    @Mock
    private CheckInProjectAggregateYearlySummaryService checkInProjectAggregateYearlySummaryService;

    @InjectMocks
    private CheckInProjectAggregateYearlySummaryTrigger checkInProjectAggregateYearlySummaryTrigger;

    @Test
    void testRunAddCheckInToSummary_shouldCallService() {
        // Arrange
        var event = new CheckInProjectAddedEvent(
            "test",
            123L,
            456L,
            LocalDate.of(2025, 5, 15)
        );

        // Act
        checkInProjectAggregateYearlySummaryTrigger.runAddCheckInToSummary(event);

        // Assert
        verify(checkInProjectAggregateYearlySummaryService).addCheckInToAggregateSummary(event.getCheckInProjectId());
    }
}