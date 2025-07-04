package io.github.shazxrin.onepercentbetter.reminder.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
import io.github.shazxrin.onepercentbetter.coach.exception.CoachException;
import io.github.shazxrin.onepercentbetter.coach.model.CoachReminder;
import io.github.shazxrin.onepercentbetter.coach.service.CoachService;
import io.github.shazxrin.onepercentbetter.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {
    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private final CheckInService checkInService;
    private final NotificationService notificationService;
    private final CoachService coachService;

    public ReminderService(
        CheckInService checkInService,
        NotificationService notificationService,
        CoachService coachService
    ) {
        this.checkInService = checkInService;
        this.notificationService = notificationService;
        this.coachService = coachService;
    }

    private String generateReminderMessage(CheckIn todaysCheckIn) {
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
        return message;
    }

    public void remind() {
        CheckIn todaysCheckIn = checkInService.getTodaysCheckIn();

        try {
            CoachReminder coachReminder = coachService.promptReminder(
                todaysCheckIn.getCount(),
                todaysCheckIn.getStreak()
            );

           notificationService.sendNotification(coachReminder.title(), coachReminder.body());
           return;
        } catch (CoachException ex) {
            log.error("Error generating coach reminder.", ex);
        }

        // Fallback
        notificationService.sendNotification("Check in reminder", generateReminderMessage(todaysCheckIn));
    }
}
