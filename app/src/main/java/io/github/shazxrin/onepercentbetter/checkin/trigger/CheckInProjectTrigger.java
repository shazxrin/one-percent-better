package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.configuration.CheckInProjectProperties;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Observed
@Component
public class CheckInProjectTrigger {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectTrigger.class);

    private final CheckInProjectProperties checkInProperties;
    private final CheckInProjectService checkInProjectService;
    private final ProjectService projectService;

    public CheckInProjectTrigger(
        CheckInProjectProperties checkInProperties,
        CheckInProjectService checkInProjectService,
        ProjectService projectService
    ) {
        this.checkInProperties = checkInProperties;
        this.checkInProjectService = checkInProjectService;
        this.projectService = projectService;
    }

    @Async
    @Scheduled(cron = "${app.check-in.project.schedule-cron}")
    public void runScheduledCheckInProjectsAll() {
        log.info("Running check-in schedule.");

        checkInProjectService.checkInAll(LocalDate.now());
    }

    @Async
    @EventListener(
        classes = ApplicationReadyEvent.class,
        condition = "#{checkInProjectProperties.bootstrap.enabled}"
    )
    public void runBootstrapCheckInProjectsAll() {
        log.info("Running check-in bootstrap.");
        for (String project : checkInProperties.getBootstrap().getProjects()) {
            projectService.addProject(project);
        }

        var bootstrapDate = LocalDate.parse(checkInProperties.getBootstrap().getDate());
        checkInProjectService.checkInAllInterval(bootstrapDate, LocalDate.now());
    }
}
