package io.github.shazxrin.onepercentbetter.reminder.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
import io.github.shazxrin.onepercentbetter.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {

    @Mock
    private CheckInService checkInService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MainReminderService reminderService;

    @Test
    void testRemind_whenTodaysCheckInHasCountGreaterThanZero_shouldSendKeepItUpNotification() {
        // Given
        CheckIn checkIn = new CheckIn();
        checkIn.setCount(5);
        checkIn.setStreak(3);
        when(checkInService.getTodaysCheckIn()).thenReturn(checkIn);

        // When
        reminderService.remind();

        // Then
        verify(notificationService, times(1)).sendNotification("Check in reminder", "You have checked in today. Keep it up!");
    }

    @Test
    void testRemind_whenTodaysCheckInHasCountZeroAndStreakGreaterThanZero_shouldSendExtendStreakNotification() {
        // Given
        CheckIn checkIn = new CheckIn();
        checkIn.setCount(0);
        checkIn.setStreak(3);
        when(checkInService.getTodaysCheckIn()).thenReturn(checkIn);

        // When
        reminderService.remind();

        // Then
        verify(notificationService, times(1)).sendNotification("Check in reminder", "You haven't check in today. Time to extend your streak!");
    }

    @Test
    void testRemind_whenTodaysCheckInHasCountZeroAndStreakZero_shouldSendStartStreakNotification() {
        // Given
        CheckIn checkIn = new CheckIn();
        checkIn.setCount(0);
        checkIn.setStreak(0);
        when(checkInService.getTodaysCheckIn()).thenReturn(checkIn);

        // When
        reminderService.remind();

        // Then
        verify(notificationService, times(1)).sendNotification("Check in reminder", "You haven't check in today. Time to start your streak!");
    }
}