package io.github.shazxrin.onepercentbetter.reminder.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.coach.exception.CoachException;
import io.github.shazxrin.onepercentbetter.coach.model.CoachReminder;
import io.github.shazxrin.onepercentbetter.coach.service.CoachService;
import io.github.shazxrin.onepercentbetter.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {

    // @Mock
    // private CheckInProjectService checkInService;

    // @Mock
    // private NotificationService notificationService;

    // @Mock
    // private CoachService coachService;

    // @InjectMocks
    // private ReminderService reminderService;

    // @Test
    // void testRemind_shouldPromptCoachForReminder() {
    //     // Given
    //     CheckInProject checkIn = new CheckInProject();
    //     checkIn.setCount(5);
    //     checkIn.setStreak(3);
    //     when(checkInService.getTodaysCheckIn()).thenReturn(checkIn);

    //     CoachReminder coachReminder = new CoachReminder("Title", "Message");
    //     when(coachService.promptReminder(anyInt(), anyInt())).thenReturn(coachReminder);

    //     // When
    //     reminderService.remind();

    //     // Then
    //     verify(coachService, times(1)).promptReminder(checkIn.getCount(), checkIn.getStreak());
    //     verify(notificationService, times(1)).sendNotification(coachReminder.title(), coachReminder.body());
    // }

    // @Test
    // void testRemind_whenCoachServiceIsDownAndTodaysCheckInHasCountGreaterThanZero_shouldSendKeepItUpNotification() {
    //     // Given
    //     CheckInProject checkIn = new CheckInProject();
    //     checkIn.setCount(5);
    //     checkIn.setStreak(3);
    //     when(checkInService.getTodaysCheckIn()).thenReturn(checkIn);

    //     when(coachService.promptReminder(anyInt(), anyInt())).thenThrow(new CoachException("Error occurred."));

    //     // When
    //     reminderService.remind();

    //     // Then
    //     verify(notificationService, times(1)).sendNotification("Check in reminder", "You have checked in today. Keep it up!");
    // }

    // @Test
    // void testRemind_whenCoachServiceThrowsTodaysCheckInHasCountZeroAndStreakGreaterThanZero_shouldSendExtendStreakNotification() {
    //     // Given
    //     CheckInProject checkIn = new CheckInProject();
    //     checkIn.setCount(0);
    //     checkIn.setStreak(3);
    //     when(checkInService.getTodaysCheckIn()).thenReturn(checkIn);

    //     when(coachService.promptReminder(anyInt(), anyInt())).thenThrow(new CoachException("Error occurred."));

    //     // When
    //     reminderService.remind();

    //     // Then
    //     verify(notificationService, times(1)).sendNotification("Check in reminder", "You haven't check in today. Time to extend your streak!");
    // }

    // @Test
    // void testRemind_whenCoachServiceThrowsTodaysCheckInHasCountZeroAndStreakZero_shouldSendStartStreakNotification() {
    //     // Given
    //     CheckInProject checkIn = new CheckInProject();
    //     checkIn.setCount(0);
    //     checkIn.setStreak(0);
    //     when(checkInService.getTodaysCheckIn()).thenReturn(checkIn);

    //     when(coachService.promptReminder(anyInt(), anyInt())).thenThrow(new CoachException("Error occurred."));

    //     // When
    //     reminderService.remind();

    //     // Then
    //     verify(notificationService, times(1)).sendNotification("Check in reminder", "You haven't check in today. Time to start your streak!");
    // }
}