package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectYearlySummaryService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectYearlySummaryTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectYearlySummaryTrigger.class);

    private final CheckInProjectYearlySummaryService checkInProjectYearlySummaryService;

    public CheckInProjectYearlySummaryTrigger(CheckInProjectYearlySummaryService checkInProjectYearlySummaryService) {
        this.checkInProjectYearlySummaryService = checkInProjectYearlySummaryService;
    }

    @Async
    @EventListener
    public void runAddCheckInToSummary(CheckInProjectAddedEvent event) {
        log.info("Running add checkin yearly summary for project {} sent at {}", event.getProjectId(), event.getDateTime());

        checkInProjectYearlySummaryService.addCheckInToSummary(event.getProjectId(), event.getCheckInProjectId());
    }

    @Async
    @Scheduled(cron = "@yearly")
    public void runScheduledInitYearlySummaries() {
        log.info("Running init yearly summaries schedule.");

        checkInProjectYearlySummaryService.initSummaries();
    }
}