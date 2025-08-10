package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Observed
@Service
public class CheckInProjectDailySummaryService {
    private final CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;
    private final CheckInProjectRepository checkInProjectRepository;

    private final ProjectService projectService;

    public CheckInProjectDailySummaryService(
        CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository,
        CheckInProjectRepository checkInProjectRepository,
        ProjectService projectService
    ) {
        this.checkInProjectDailySummaryRepository = checkInProjectDailySummaryRepository;
        this.checkInProjectRepository = checkInProjectRepository;
        this.projectService = projectService;
    }

    public CheckInProjectDailySummary getSummary(long projectId, LocalDate date) {
        var project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        var summaryOpt = checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date);
        if (summaryOpt.isPresent()) {
            return summaryOpt.get();
        }

        var newSummary = new CheckInProjectDailySummary(
            date,
            0,
            0,
            project
        );
        return checkInProjectDailySummaryRepository.save(newSummary);
    }

    public void calculateSummaryForDate(long projectId, LocalDate date, boolean withCount) {
        Project project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        LocalDate previousDate = date.minusDays(1);

        var previousDateSummaryOpt = checkInProjectDailySummaryRepository
            .findByProjectIdAndDate(projectId, previousDate);
        var currentDateSummary = checkInProjectDailySummaryRepository
            .findByProjectIdAndDate(projectId, date)
            .orElse(new CheckInProjectDailySummary(date, 0, 0, project));

        // If with count enabled, the check ins for the project and date will be fetched
        // Else it will use the existing count
        int noOfCheckIns = currentDateSummary.getNoOfCheckIns();
        if (withCount) {
            noOfCheckIns = checkInProjectRepository.countByProjectIdAndDate(projectId, date);
        }

        int currentStreak = 0;
        if (noOfCheckIns > 0) {
            currentStreak = previousDateSummaryOpt
                .map(CheckInProjectDailySummary::getStreak)
                .orElse(0) + 1;
        }

        currentDateSummary.setNoOfCheckIns(noOfCheckIns);
        currentDateSummary.setStreak(currentStreak);

        checkInProjectDailySummaryRepository.save(currentDateSummary);
    }

    @Transactional
    public void addCheckInToSummary(long projectId, LocalDate date) {
         projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        LocalDate previousDate = date.minusDays(1);

        var previousDateSummaryOpt = checkInProjectDailySummaryRepository
            .findByProjectIdAndDate(projectId, previousDate);
        var currentDateSummary = checkInProjectDailySummaryRepository
            .findByProjectIdAndDateWithLock(projectId, date)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given date. Cannot lock and update."));

        int noOfCheckIns = currentDateSummary.getNoOfCheckIns() + 1;
        int currentStreak = previousDateSummaryOpt
            .map(CheckInProjectDailySummary::getStreak)
            .orElse(0) + 1;

        currentDateSummary.setNoOfCheckIns(noOfCheckIns);
        currentDateSummary.setStreak(currentStreak);

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
