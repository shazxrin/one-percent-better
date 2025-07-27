package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectDailySummaryService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectDailySummarySchedule {
    private final CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    public CheckInProjectDailySummarySchedule(CheckInProjectDailySummaryService checkInProjectDailySummaryService) {
        this.checkInProjectDailySummaryService = checkInProjectDailySummaryService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledInitDailySummaries() {
        checkInProjectDailySummaryService.initSummaries(LocalDate.now());
    }
}
