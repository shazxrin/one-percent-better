package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.event.CheckInProjectDailySummaryUpdateEvent;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectAggregateDailySummaryService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
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
    @EventListener
    public void runCalculateDailySummary(CheckInProjectDailySummaryUpdateEvent event) {
        log.info("Running calculate aggregate daily summary");
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(event.getDate());
    }

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledInitAggregateDailySummary() {
        checkInProjectAggregateDailySummaryService.initAggregateSummary(LocalDate.now());
    }
}
