package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectScheduleTest {

    @Mock
    private CheckInProjectService checkInProjectService;

    @InjectMocks
    private CheckInProjectSchedule checkInProjectSchedule;

    @Test
    void testRunScheduledCheckInProjectsAll_shouldCallCheckInServiceCheckInToday() {
        // When
        checkInProjectSchedule.runScheduledCheckInProjectsAll();

        // Then
        verify(checkInProjectService, times(1)).checkInAll(eq(LocalDate.now()));
    }
}
