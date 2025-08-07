package io.github.shazxrin.onepercentbetter.checkin.summary.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectDailySummaryService;
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
public class CheckInProjectDailySummaryTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectDailySummaryTrigger.class);

    private final CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    public CheckInProjectDailySummaryTrigger(
        CheckInProjectDailySummaryService checkInProjectDailySummaryService
    ) {
        this.checkInProjectDailySummaryService = checkInProjectDailySummaryService;
    }

    @Async
    @EventListener
    public void runAddCheckInToSummary(CheckInProjectAddedEvent event) {
        log.info("Running add checkin daily summary for project: {}", event.getProjectId());

        checkInProjectDailySummaryService.addCheckInToSummary(event.getProjectId(), event.getDate());
    }

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledInitDailySummaries() {
        checkInProjectDailySummaryService.initSummaries(LocalDate.now());
    }
}
