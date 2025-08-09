package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectAggregateDailySummaryService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectAggregateDailySummaryTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectAggregateDailySummaryTrigger.class);

    private final CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;

    public CheckInProjectAggregateDailySummaryTrigger(
        CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService
    ) {
        this.checkInProjectAggregateDailySummaryService = checkInProjectAggregateDailySummaryService;
    }

    @Async
    @Scheduled(cron = "@yearly")
    public void runScheduledInitAggregateDailySummary() {
        log.info("Running init aggregate daily summaries schedule.");

        checkInProjectAggregateDailySummaryService.initAggregateSummaries();
    }
}
