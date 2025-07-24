package io.github.shazxrin.onepercentbetter.reminder.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.coach.exception.CoachException;
import io.github.shazxrin.onepercentbetter.coach.model.CoachReminder;
import io.github.shazxrin.onepercentbetter.coach.service.CoachService;
import io.github.shazxrin.onepercentbetter.notification.service.NotificationService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Observed
@Service
public class ReminderService {
    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private final CheckInProjectService checkInService;
    private final NotificationService notificationService;
    private final CoachService coachService;

    public ReminderService(
        CheckInProjectService checkInService,
        NotificationService notificationService,
        CoachService coachService
    ) {
        this.checkInService = checkInService;
        this.notificationService = notificationService;
        this.coachService = coachService;
    }

    private String generateReminderMessage(CheckInProject todaysCheckIn) {
        // String message;
        // if (todaysCheckIn.getCount() > 0) {
        //     message = "You have checked in today. Keep it up!";
        // } else {
        //     if (todaysCheckIn.getStreak() > 0) {
        //         message = "You haven't check in today. Time to extend your streak!";
        //     } else {
        //         message = "You haven't check in today. Time to start your streak!";
        //     }
        // }
        // return message;
        return null;
    }

    public void remind() {
        // CheckInProject todaysCheckIn = checkInService.getTodaysCheckIn();

        // try {
        //     CoachReminder coachReminder = coachService.promptReminder(
        //         todaysCheckIn.getCount(),
        //         todaysCheckIn.getStreak()
        //     );

        //    notificationService.sendNotification(coachReminder.title(), coachReminder.body());
        //    return;
        // } catch (CoachException ex) {
        //     log.error("Error generating coach reminder.", ex);
        // }

        // // Fallback
        // notificationService.sendNotification("Check in reminder", generateReminderMessage(todaysCheckIn));
    }
}
