package io.github.shazxrin.onepercentbetter.checkin.schedule;

import io.github.shazxrin.onepercentbetter.checkin.configuration.CheckInProperties;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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

    @EventListener(ApplicationReadyEvent.class)
    public void checkInBootstrap() {
        log.info("Running check-in bootstrap.");
        for (String project : checkInProperties.getBootstrapProjects()) {
            String[] projectSplit = project.split("/");
            if (projectSplit.length != 2) {
                log.error("Invalid project format: {}.", project);
                continue;
            }

            projectService.addProject(projectSplit[0], projectSplit[1]);
        }

        var bootstrapDate = LocalDate.parse(checkInProperties.getBootstrapDate());
        checkInService.checkInInterval(bootstrapDate, LocalDate.now());
    }
}
