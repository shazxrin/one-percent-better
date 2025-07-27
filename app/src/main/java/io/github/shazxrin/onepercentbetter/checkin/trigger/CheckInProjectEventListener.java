package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectDailySummaryService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectEventListener {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectEventListener.class);
    private final CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    public CheckInProjectEventListener(CheckInProjectDailySummaryService checkInProjectDailySummaryService) {
        this.checkInProjectDailySummaryService = checkInProjectDailySummaryService;
    }

    @Async
    @EventListener
    public void runCalculateDailySummary(CheckInProjectAddedEvent event) {
        log.info("Running calculate daily summary for project: {}", event.getProjectId());
        checkInProjectDailySummaryService.calculateSummary(event.getProjectId(), event.getDate());
    }
}
