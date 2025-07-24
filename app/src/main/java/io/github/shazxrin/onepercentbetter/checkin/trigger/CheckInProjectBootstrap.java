package io.github.shazxrin.onepercentbetter.checkin.trigger;

import io.github.shazxrin.onepercentbetter.checkin.configuration.CheckInProjectProperties;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Observed
@ConditionalOnProperty("app.check-in.bootstrap.enabled")
@Component
public class CheckInProjectBootstrap {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectBootstrap.class);

    private final CheckInProjectProperties checkInProperties;
    private final CheckInProjectService checkInProjectService;
    private final ProjectService projectService;

    public CheckInProjectBootstrap(
        CheckInProjectProperties checkInProperties,
        CheckInProjectService checkInService,
        ProjectService projectService
    ) {
        this.checkInProperties = checkInProperties;
        this.checkInProjectService = checkInService;
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
        checkInProjectService.checkInAllInterval(bootstrapDate, LocalDate.now());
    }
}
