package io.github.shazxrin.onepercentbetter.checkin.core.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectTrigger.class);

    private final CheckInProjectService checkInProjectService;

    public CheckInProjectTrigger(CheckInProjectService checkInProjectService) {
        this.checkInProjectService = checkInProjectService;
    }

    @Async
    @Scheduled(cron = "${app.check-in.project.schedule-cron}")
    public void runScheduledCheckInProjectsAll() {
        log.info("Running check-in schedule.");

        checkInProjectService.checkInAll(LocalDate.now());
    }
}
