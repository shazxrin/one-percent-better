package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service.CheckInProjectAggregateMonthlySummaryService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectAggregateMonthlySummaryTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectAggregateMonthlySummaryTrigger.class);
    private final CheckInProjectAggregateMonthlySummaryService checkInProjectAggregateMonthlySummaryService;

    public CheckInProjectAggregateMonthlySummaryTrigger(CheckInProjectAggregateMonthlySummaryService checkInProjectAggregateMonthlySummaryService) {
        this.checkInProjectAggregateMonthlySummaryService = checkInProjectAggregateMonthlySummaryService;
    }

    @Async
    @EventListener
    public void runAddCheckInToSummary(CheckInProjectAddedEvent event) {
        log.info("Running add checkin aggregate monthly summary for project {} sent at {}", event.getProjectId(), event.getDateTime());

        checkInProjectAggregateMonthlySummaryService.addCheckInToAggregateSummary(event.getCheckInProjectId());
    }

    @Async
    @Scheduled(cron = "@yearly")
    public void runScheduledInitAggregateMonthlySummaries() {
        log.info("Running init aggregate monthly summaries schedule.");

        checkInProjectAggregateMonthlySummaryService.initAggregateSummaries();
    }
}