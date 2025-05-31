package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CheckInScheduleTest {

    @Mock
    private CheckInService checkInService;

    @InjectMocks
    private CheckInSchedule checkInSchedule;

    @Test
    void testCheckIn_shouldCallCheckInServiceCheckInToday() {
        // When
        checkInSchedule.checkIn();

        // Then
        verify(checkInService, times(1)).checkInToday();
    }
}
