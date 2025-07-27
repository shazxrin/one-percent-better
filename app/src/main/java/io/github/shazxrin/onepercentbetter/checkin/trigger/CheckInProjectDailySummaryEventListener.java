package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.event.CheckInProjectDailySummaryUpdateEvent;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectAggregateDailySummaryService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectDailySummaryEventListener {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectDailySummaryEventListener.class);

    private final CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;

    public CheckInProjectDailySummaryEventListener(
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
}
