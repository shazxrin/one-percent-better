package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectWeeklySummaryService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectWeeklySummaryTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectWeeklySummaryTrigger.class);
    private final CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService;

    public CheckInProjectWeeklySummaryTrigger(CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService) {
        this.checkInProjectWeeklySummaryService = checkInProjectWeeklySummaryService;
    }

    @Async
    @EventListener
    public void runAddCheckInToSummary(CheckInProjectAddedEvent event) {
        log.info("Running add checkin weekly summary for project {} sent at {}", event.getProjectId(), event.getDateTime());

        checkInProjectWeeklySummaryService.addCheckInToSummary(event.getProjectId(), event.getCheckInProjectId());
    }

    @Async
    @Scheduled(cron = "@yearly")
    public void runScheduledInitWeeklySummaries() {
        log.info("Running init weekly summaries schedule.");

        checkInProjectWeeklySummaryService.initSummaries();
    }
}
