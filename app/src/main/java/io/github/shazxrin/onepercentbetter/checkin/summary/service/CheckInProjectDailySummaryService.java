package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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

    public void initSummaries(LocalDate date) {
        for (Project project : projectService.getAllProjects()) {
            var newSummary = new CheckInProjectDailySummary(
                date,
                0,
                0,
                project
            );

            checkInProjectDailySummaryRepository.save(newSummary);
        }
    }

    public CheckInProjectDailySummary getSummary(long projectId, LocalDate date) {
        var project = projectService.getProjectById(projectId);

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

    public void calculateSummary(long projectId, LocalDate date) {
        Project project = projectService.getProjectById(projectId);

        LocalDate previousDate = date.minusDays(1);

        var previousDateSummaryOpt = checkInProjectDailySummaryRepository
            .findByProjectIdAndDate(projectId, previousDate);
        var currentDateSummary = checkInProjectDailySummaryRepository
            .findByProjectIdAndDate(projectId, date)
            .orElse(new CheckInProjectDailySummary(date, 0, 0, project));

        int totalCommits = checkInProjectRepository.countByDate(date);
        
        int currentStreak = 0;
        if (totalCommits > 0) {
            currentStreak = previousDateSummaryOpt
                .map(CheckInProjectDailySummary::getStreak)
                .orElse(0) + 1;
        }

        currentDateSummary.setNoOfCheckIns(totalCommits);
        currentDateSummary.setStreak(currentStreak);

        checkInProjectDailySummaryRepository.save(currentDateSummary);
    }
}
