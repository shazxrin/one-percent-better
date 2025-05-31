package io.github.shazxrin.onepercentbetter.reminder.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
import io.github.shazxrin.onepercentbetter.notification.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class MainReminderService implements ReminderService {
    private final CheckInService checkInService;
    private final NotificationService notificationService;

    public MainReminderService(CheckInService checkInService, NotificationService notificationService) {
        this.checkInService = checkInService;
        this.notificationService = notificationService;
    }

    @Override
    public void remind() {
        CheckIn todaysCheckIn = checkInService.getTodaysCheckIn();

        String message;
        if (todaysCheckIn.getCount() > 0) {
            message = "You have checked in today. Keep it up!";
        } else {
            if (todaysCheckIn.getStreak() > 0) {
                message = "You haven't check in today. Time to extend your streak!";
            } else {
                message = "You haven't check in today. Time to start your streak!";
            }
        }

        notificationService.sendNotification("Check in reminder", message);
    }
}
