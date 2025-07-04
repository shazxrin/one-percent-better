package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.configuration.CheckInProperties;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@ConditionalOnProperty("app.check-in.bootstrap.enabled")
@Component
public class CheckInBootstrap {
    private static final Logger log = LoggerFactory.getLogger(CheckInBootstrap.class);

    private final CheckInProperties checkInProperties;
    private final CheckInService checkInService;
    private final ProjectService projectService;

    public CheckInBootstrap(
        CheckInProperties checkInProperties,
        CheckInService checkInService,
        ProjectService projectService
    ) {
        this.checkInProperties = checkInProperties;
        this.checkInService = checkInService;
        this.projectService = projectService;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void checkInBootstrap() {
        log.info("Running check-in bootstrap.");
        for (String project : checkInProperties.getBootstrap().getProjects()) {
            projectService.addProject(project);
        }

        var bootstrapDate = LocalDate.parse(checkInProperties.getBootstrap().getDate());
        checkInService.checkInInterval(bootstrapDate, LocalDate.now());
    }
}
