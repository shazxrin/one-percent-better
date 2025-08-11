package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Observed
@Service
public class CheckInProjectDailySummaryService {
    private final CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    private final ProjectService projectService;
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectDailySummaryService(
        CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository,
        CheckInProjectRepository checkInProjectRepository,
        ProjectService projectService,
        CheckInProjectService checkInProjectService
    ) {
        this.checkInProjectDailySummaryRepository = checkInProjectDailySummaryRepository;
        this.projectService = projectService;
        this.checkInProjectService = checkInProjectService;
    }

    public CheckInProjectDailySummary getSummary(long projectId, LocalDate date) {
        var project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        return checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given date."));
    }

    @Transactional
    public void calculateSummaryForDate(long projectId, LocalDate date, boolean withCount) {
        projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        LocalDate previousDate = date.minusDays(1);

        var previousDateSummary = checkInProjectDailySummaryRepository
            .findByProjectIdAndDate(projectId, previousDate)
            .orElseThrow(() -> new IllegalStateException("No summary found for the previous date."));
        var currentDateSummary = checkInProjectDailySummaryRepository
            .findByProjectIdAndDateWithLock(projectId, date)
            .orElseThrow(() -> new IllegalStateException(
                "No summary found for the previous date. Cannot lock and update."));

        // If with count enabled, the check ins for the project and date will be fetched
        // Else it will use the existing count
        int noOfCheckIns = currentDateSummary.getNoOfCheckIns();
        Map<String, Integer> typeDistribution = currentDateSummary.getTypeDistribution();
        if (withCount) {
            List<CheckInProject> checkIns = checkInProjectService.getAllCheckIns(projectId, date);

            // Calculate check in count
            noOfCheckIns = checkIns.size();

            // Calculate type distribution
            typeDistribution = checkIns.stream()
                .filter(c -> c.getType() != null)
                .collect(Collectors.groupingBy(
                    CheckInProject::getType,
                    Collectors.summingInt(_ -> 1)
                ));
        }

        int currentStreak = 0;
        if (noOfCheckIns > 0) {
            currentStreak = previousDateSummary.getStreak() + 1;
        }

        currentDateSummary.setNoOfCheckIns(noOfCheckIns);
        currentDateSummary.setStreak(currentStreak);
        currentDateSummary.setTypeDistribution(typeDistribution);

        checkInProjectDailySummaryRepository.save(currentDateSummary);
    }

    @Transactional
    public void addCheckInToSummary(long projectId, long checkInProjectId, LocalDate date) {
        projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        var checkInProject = checkInProjectService.getCheckIn(checkInProjectId)
            .orElseThrow(CheckInProjectNotFoundException::new);

        LocalDate previousDate = date.minusDays(1);

        var previousDateSummary = checkInProjectDailySummaryRepository
            .findByProjectIdAndDate(projectId, previousDate)
            .orElseThrow(() -> new IllegalStateException("No summary found for the previous date."));
        var currentDateSummary = checkInProjectDailySummaryRepository
            .findByProjectIdAndDateWithLock(projectId, date)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given date. Cannot lock and update."));

        int noOfCheckIns = currentDateSummary.getNoOfCheckIns() + 1;
        int currentStreak = previousDateSummary.getStreak() + 1;
        int typeCount = currentDateSummary.getTypeDistribution().getOrDefault(checkInProject.getType(), 0) + 1;

        currentDateSummary.setNoOfCheckIns(noOfCheckIns);
        currentDateSummary.setStreak(currentStreak);
        currentDateSummary.getTypeDistribution().put(checkInProject.getType(), typeCount);

        checkInProjectDailySummaryRepository.save(currentDateSummary);

        // Recalculate dates after the current date (if any)
        // Streak may have changed from the current date onwards
        var today = LocalDate.now();
        var currentDate = date.plusDays(1);
        while (!currentDate.isAfter(today)) {
            calculateSummaryForDate(projectId, currentDate, false);

            currentDate = currentDate.plusDays(1);
        }
    }

    public void initSummary(long projectId) {
        var project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        var now = LocalDate.now();

        List<CheckInProjectDailySummary> summaries = new ArrayList<>();
        for (int i = 1; i <= now.lengthOfYear(); i++) {
            summaries.add(new CheckInProjectDailySummary(now.withDayOfYear(i), 0, 0, project));
        }

        checkInProjectDailySummaryRepository.saveAll(summaries);
    }

    public void initSummaries() {
        var projects = projectService.getAllProjects();

        for (Project project : projects) {
            initSummary(project.getId());
        }
    }
}
