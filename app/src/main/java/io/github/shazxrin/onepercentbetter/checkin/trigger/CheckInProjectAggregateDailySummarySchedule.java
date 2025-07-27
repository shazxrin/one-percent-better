package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectAggregateDailySummaryService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectAggregateDailySummarySchedule {
    private final CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;

    public CheckInProjectAggregateDailySummarySchedule(
        CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService
    ) {
        this.checkInProjectAggregateDailySummaryService = checkInProjectAggregateDailySummaryService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledInitAggregateDailySummary() {
        checkInProjectAggregateDailySummaryService.initAggregateSummary(LocalDate.now());
    }
}
