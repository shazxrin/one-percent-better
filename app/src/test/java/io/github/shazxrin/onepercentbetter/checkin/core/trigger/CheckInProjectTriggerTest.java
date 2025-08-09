package io.github.shazxrin.onepercentbetter.checkin.core.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectTriggerTest {
    @Mock
    private CheckInProjectService checkInProjectService;

    @InjectMocks
    private CheckInProjectTrigger checkInProjectTrigger;

    @Test
    void testRunScheduledCheckInProjectsAll_shouldCallCheckInServiceCheckInToday() {
        // When
        checkInProjectTrigger.runScheduledCheckInProjectsAll();

        // Then
        verify(checkInProjectService, times(1)).checkInAll(eq(LocalDate.now()));
    }
}
