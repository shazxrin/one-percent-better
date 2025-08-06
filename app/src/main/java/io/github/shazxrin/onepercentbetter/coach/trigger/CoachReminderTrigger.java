package io.github.shazxrin.onepercentbetter.coach.trigger;

import io.github.shazxrin.onepercentbetter.coach.service.CoachService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CoachReminderTrigger {
    private static final Logger log = LoggerFactory.getLogger(CoachReminderTrigger.class);
    private final CoachService coachService;

    public CoachReminderTrigger(CoachService coachService) {
        this.coachService = coachService;
    }

    @Async
    @Scheduled(cron = "${app.coach.reminder.schedule-cron}")
    public void runScheduledRemindUserProgress() {
        log.info("Running reminder schedule.");

        coachService.remindUserProgress();
    }
}
