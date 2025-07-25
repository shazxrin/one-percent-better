package io.github.shazxrin.onepercentbetter.checkin.service;

import io.github.shazxrin.onepercentbetter.checkin.exception.CheckInProjectDailySummaryNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
        return checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)
                .orElseThrow(() -> new CheckInProjectDailySummaryNotFoundException("Check in project daily summary not found!"));
    }

    public void calculateSummary(long projectId, LocalDate date) {
        Project project = projectService.getProjectById(projectId);

        LocalDate previousDate = date.minusDays(1);

        var previousDateSummaryOpt = checkInProjectDailySummaryRepository
                .findByProjectIdAndDate(projectId, previousDate);
        var currentDateSummary = checkInProjectDailySummaryRepository
                .findByProjectIdAndDate(projectId, date)
                .orElse(new CheckInProjectDailySummary(date, 0, 0, project));

        int currentStreak = previousDateSummaryOpt
                .map(CheckInProjectDailySummary::getStreak)
                .orElse(0);

        int totalCommits = checkInProjectRepository.countByDate(date);
        if (totalCommits > 0) {
            currentStreak++;
        }

        currentDateSummary.setNoOfCheckIns(totalCommits);
        currentDateSummary.setStreak(currentStreak);

        checkInProjectDailySummaryRepository.save(currentDateSummary);
    }
}
