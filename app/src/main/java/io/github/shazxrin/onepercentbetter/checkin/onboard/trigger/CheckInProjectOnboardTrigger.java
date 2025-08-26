package io.github.shazxrin.onepercentbetter.checkin.onboard.trigger;

import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.daily.service.CheckInProjectDailySummaryService;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.service.CheckInProjectWeeklySummaryService;
import io.github.shazxrin.onepercentbetter.project.event.ProjectAddedEvent;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectOnboardTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectOnboardTrigger.class);
    private final CheckInProjectService checkInProjectService;
    private final CheckInProjectDailySummaryService checkInProjectDailySummaryService;
    private final CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService;

    public CheckInProjectOnboardTrigger(
        CheckInProjectService checkInProjectService,
        CheckInProjectDailySummaryService checkInProjectDailySummaryService,
        CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService
    ) {
        this.checkInProjectService = checkInProjectService;
        this.checkInProjectDailySummaryService = checkInProjectDailySummaryService;
        this.checkInProjectWeeklySummaryService = checkInProjectWeeklySummaryService;
    }

    @Async
    @EventListener
    public void runOnboardProjectCheckIns(ProjectAddedEvent event) {
        log.info("Running onboard project check-ins for project: {}", event.getProjectId());

        var now = LocalDate.now();
        var firstDayOfYear = now.withDayOfYear(1);

        // Init summaries for first day of year to last day of year for project
        checkInProjectDailySummaryService.initSummary(event.getProjectId());
        checkInProjectWeeklySummaryService.initSummary(event.getProjectId());

        // Check in current project from first day of year to now
        checkInProjectService.checkInInterval(event.getProjectId(), firstDayOfYear, now);
    }
}
