package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectSchedule {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectSchedule.class);

    private final CheckInProjectService checkInProjectService;

    public CheckInProjectSchedule(CheckInProjectService checkInService) {
        this.checkInProjectService = checkInService;
    }

    @Scheduled(cron = "${app.check-in.project.schedule-cron}")
    public void runScheduledCheckInProjectsAll() {
        log.info("Running check-in schedule.");

        checkInProjectService.checkInAll(LocalDate.now());
    }
}
