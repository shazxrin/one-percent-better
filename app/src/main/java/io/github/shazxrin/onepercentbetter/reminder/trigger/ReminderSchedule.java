package io.github.shazxrin.onepercentbetter.reminder.trigger;

import io.github.shazxrin.onepercentbetter.reminder.service.ReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderSchedule {
    private static final Logger log = LoggerFactory.getLogger(ReminderSchedule.class);
    private final ReminderService reminderService;

    public ReminderSchedule(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Scheduled(cron = "${app.reminder.schedule-cron}")
    public void runScheduledReminder() {
        log.info("Running reminder schedule.");

        reminderService.remind();
    }
}
