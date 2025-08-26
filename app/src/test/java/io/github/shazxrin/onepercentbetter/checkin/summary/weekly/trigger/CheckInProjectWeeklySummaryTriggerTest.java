package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.service.CheckInProjectWeeklySummaryService;
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
public class CheckInProjectWeeklySummaryTriggerTest {
    @Mock
    private CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService;

    @InjectMocks
    private CheckInProjectWeeklySummaryTrigger trigger;

    @Test
    void testRunAddCheckInToSummary_shouldCallAddCheckInToSummaryOnService() {
        long projectId = 1L;
        long checkInProjectId = 2L;
        LocalDate date = LocalDate.of(2025, 7, 31);
        CheckInProjectAddedEvent event = new CheckInProjectAddedEvent(this, projectId, checkInProjectId, date);

        trigger.runAddCheckInToSummary(event);

        verify(checkInProjectWeeklySummaryService, times(1)).addCheckInToSummary(eq(projectId), eq(checkInProjectId));
    }

    @Test
    void testRunScheduledInitWeeklySummaries_shouldCallInitSummariesOnService() {
        trigger.runScheduledInitWeeklySummaries();
        verify(checkInProjectWeeklySummaryService, times(1)).initSummaries();
    }
}
