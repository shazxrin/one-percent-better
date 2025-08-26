package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.service.CheckInProjectAggregateWeeklySummaryService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectAggregateWeeklySummaryTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectAggregateWeeklySummaryTrigger.class);
    private final CheckInProjectAggregateWeeklySummaryService checkInProjectAggregateWeeklySummaryService;

    public CheckInProjectAggregateWeeklySummaryTrigger(CheckInProjectAggregateWeeklySummaryService checkInProjectAggregateWeeklySummaryService) {
        this.checkInProjectAggregateWeeklySummaryService = checkInProjectAggregateWeeklySummaryService;
    }

    @Async
    @EventListener
    public void runAddCheckInToSummary(CheckInProjectAddedEvent event) {
        log.info("Running add checkin aggregate weekly summary for project {} sent at {}", event.getProjectId(), event.getDateTime());

        checkInProjectAggregateWeeklySummaryService.addCheckInToAggregateSummary(event.getCheckInProjectId());
    }

    @Async
    @Scheduled(cron = "@yearly")
    public void runScheduledInitAggregateWeeklySummaries() {
        log.info("Running init aggregate weekly summaries schedule.");

        checkInProjectAggregateWeeklySummaryService.initAggregateSummaries();
    }
}
