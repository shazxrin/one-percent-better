package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectAggregateYearlySummaryService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectAggregateYearlySummaryTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectAggregateYearlySummaryTrigger.class);
    private final CheckInProjectAggregateYearlySummaryService checkInProjectAggregateYearlySummaryService;

    public CheckInProjectAggregateYearlySummaryTrigger(CheckInProjectAggregateYearlySummaryService checkInProjectAggregateYearlySummaryService) {
        this.checkInProjectAggregateYearlySummaryService = checkInProjectAggregateYearlySummaryService;
    }

    @Async
    @EventListener
    public void runAddCheckInToSummary(CheckInProjectAddedEvent event) {
        log.info("Running add checkin aggregate yearly summary for project {} sent at {}", event.getProjectId(), event.getDateTime());

        checkInProjectAggregateYearlySummaryService.addCheckInToAggregateSummary(event.getCheckInProjectId());
    }

    @Async
    @Scheduled(cron = "@yearly")
    public void runScheduledInitCurrentYearAggregateSummary() {
        log.info("Running init current year aggregate yearly summary schedule.");

        checkInProjectAggregateYearlySummaryService.initAggregateSummary(LocalDateTime.now().getYear());
    }
}