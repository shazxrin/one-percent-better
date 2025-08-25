package io.github.shazxrin.onepercentbetter.checkin.core.service;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.github.model.Commit;
import io.github.shazxrin.onepercentbetter.github.service.GitHubService;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.github.shazxrin.onepercentbetter.utils.project.ProjectOwnerName;
import io.github.shazxrin.onepercentbetter.utils.project.ProjectUtil;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Observed
@Service
public class CheckInProjectService {
    private static final Logger log = LoggerFactory.getLogger(CheckInProjectService.class);

    private final CheckInProjectRepository checkInProjectRepository;
    private final ProjectService projectService;
    private final GitHubService gitHubService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CheckInProjectService(
        CheckInProjectRepository checkInRepository,
        ProjectService projectService,
        GitHubService gitHubService,
        ApplicationEventPublisher applicationEventPublisher
    ) {
        this.checkInProjectRepository = checkInRepository;
        this.projectService = projectService;
        this.gitHubService = gitHubService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private void checkInProject(Project project, LocalDate date) {
        log.info("Checking in project {} for date {}.", project.getName(), date);
        ProjectOwnerName projectOwnerName;
        try {
            projectOwnerName = ProjectUtil.parseProjectRepoOwnerName(project.getName());
        } catch (IllegalArgumentException ex) {
            log.error("Invalid project name {}.", project.getName(), ex);
            return;
        }

        List<Commit> commits = gitHubService.getCommitsForRespositoryOnDate(
            projectOwnerName.owner(),
            projectOwnerName.name(),
            date
        );

        for (Commit commit : commits) {
            log.info("Checking in commit {} for project {}.", commit.sha(), project.getName());

            if (checkInProjectRepository.existsByProjectIdAndHash(project.getId(), commit.sha())) {
                log.info("Commit {} already checked in for project {}. Skipping.", commit.sha(), project.getName());
                continue;
            }

            String commitType = "unknown";
            String commitMessage = "";
            String[] commitMessageSplit = commit.commit().message().split(": ");
            if (commitMessageSplit.length == 2) {
                commitType = commitMessageSplit[0];
                commitMessage = commitMessageSplit[1];
            } else {
                commitMessage = commit.commit().message();
            }

            CheckInProject checkInProject = checkInProjectRepository.save(
                new CheckInProject(
                    commit.commit().committer().date().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                    commit.sha(),
                    commitType,
                    commitMessage,
                    project
                )
            );

            applicationEventPublisher.publishEvent(
                new CheckInProjectAddedEvent(this, project.getId(), checkInProject.getId(), date)
            );
        }
    }

    public void checkIn(long projectId, LocalDate date) {
        Project project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        checkInProject(project, date);
    }

    public void checkInInterval(long projectId, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before to date.");
        }

        Project project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        var currentDate = from;
        while (!currentDate.isAfter(to)) {
            checkInProject(project, currentDate);
            currentDate = currentDate.plusDays(1);
        }
    }

    public void checkInAll(LocalDate date) {
        List<Project> projects = projectService.getAllProjects();
        for (Project project : projects) {
            checkInProject(project, date);
        }
    }

    public void checkInAllInterval(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before to date.");
        }

        List<Project> projects = projectService.getAllProjects();

        var currentDate = from;
        while (!currentDate.isAfter(to)) {
            for (Project project : projects) {
                checkInProject(project, currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    public Optional<CheckInProject> getCheckIn(long id) {
        return checkInProjectRepository.findById(id);
    }

    public List<CheckInProject> getAllCheckInsByProject(long projectId, LocalDate date) {
        return checkInProjectRepository.findByProjectIdAndDateTimeBetween(projectId, date.atTime(LocalTime.MIN), date.atTime(LocalTime.MAX));
    }

    public List<CheckInProject> getAllCheckIns(LocalDate date) {
        return checkInProjectRepository.findByDateTimeBetween(date.atTime(LocalTime.MIN), date.atTime(LocalTime.MAX));
    }

    public List<CheckInProject> getAllCheckInsByProjectBetween(long projectId, LocalDate from, LocalDate to) {
        return checkInProjectRepository.findByProjectIdAndDateTimeBetween(projectId, from.atTime(LocalTime.MIN), to.atTime(LocalTime.MAX));
    }

    public List<CheckInProject> getAllCheckInsBetween(LocalDate from, LocalDate to) {
        return checkInProjectRepository.findByDateTimeBetween(from.atTime(LocalTime.MIN), to.atTime(LocalTime.MAX));
    }
}
