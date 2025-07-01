package io.github.shazxrin.onepercentbetter.reminder.trigger;

import io.github.shazxrin.onepercentbetter.reminder.service.ReminderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReminderScheduleTest {

    @Mock
    private ReminderService reminderService;

    @InjectMocks
    private ReminderSchedule reminderSchedule;

    @Test
    void testRunScheduledReminder_shouldCallReminderServiceRemind() {
        // When
        reminderSchedule.runScheduledReminder();

        // Then
        verify(reminderService, times(1)).remind();
    }
}