package io.github.shazxrin.onepercentbetter.schedule;

import io.github.shazxrin.onepercentbetter.service.checkin.CheckInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckInSchedule {
    private static final Logger log = LoggerFactory.getLogger(CheckInSchedule.class);

    private final CheckInService checkInService;

    public CheckInSchedule(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @Scheduled(cron = "${app.check-in-cron}")
    public void checkIn() {
        log.info("Running check-in schedule.");

        checkInService.checkInToday();
    }
}
