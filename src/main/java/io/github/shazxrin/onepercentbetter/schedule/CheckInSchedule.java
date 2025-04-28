package io.github.shazxrin.onepercentbetter.schedule;

import io.github.shazxrin.onepercentbetter.service.CheckInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheckInSchedule {
    private final CheckInService checkInService;

    @Scheduled(cron = "${app.check-in-cron}")
    public void checkIn() {
        log.info("Running check-in schedule...");

        checkInService.checkInToday();
    }
}
