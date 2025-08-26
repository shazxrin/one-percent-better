package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectWeeklySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectWeeklySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.utility.StreakUtility;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Observed
@Service
public class CheckInProjectWeeklySummaryService {
    private final CheckInProjectWeeklySummaryRepository checkInProjectWeeklySummaryRepository;

    private final ProjectService projectService;
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectWeeklySummaryService(
        CheckInProjectWeeklySummaryRepository checkInProjectWeeklySummaryRepository,
        ProjectService projectService,
        CheckInProjectService checkInProjectService
    ) {
        this.checkInProjectWeeklySummaryRepository = checkInProjectWeeklySummaryRepository;
        this.projectService = projectService;
        this.checkInProjectService = checkInProjectService;
    }

    public CheckInProjectWeeklySummary getSummary(long projectId, int year, int weekNo) {
        projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        return checkInProjectWeeklySummaryRepository.findByProjectIdAndYearAndWeekNo(projectId, year, weekNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project, year and weekNo."));
    }

    @Transactional
    public void calculateSummaryForWeek(long projectId, int year, int weekNo) {
        projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        var summary = checkInProjectWeeklySummaryRepository.findByProjectIdAndYearAndWeekNoWithLock(projectId, year, weekNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project, year and weekNo."));

        int noOfCheckIns;
        int streak;
        Map<String, Integer> typeDistribution = summary.getTypeDistribution();
        Map<String, Integer> hourDistribution = summary.getHourDistribution();
        Map<String, Integer> dayDistribution = summary.getDayDistribution();

        var startDate = summary.getStartDate();
        var endDate = summary.getEndDate();
        List<CheckInProject> checkIns = checkInProjectService.getAllCheckInsByProjectBetween(projectId, startDate, endDate);

        // Calculate check in count
        noOfCheckIns = checkIns.size();

        // Calculate streak
        streak = StreakUtility.calculateMaxStreakFromCheckIns(checkIns);

        // Calculate type distribution
        checkIns.stream()
            .map(c -> Objects.requireNonNullElse(c.getType(), "unknown"))
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.summingInt(_ -> 1)
            ))
            .forEach((key, value) -> typeDistribution.merge(key, value, Integer::sum));

        // Calculate hour distribution
        checkIns.stream()
            .map(c -> String.valueOf(c.getDateTime().getHour()))
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.summingInt(_ -> 1)
            ))
            .forEach((key, value) -> hourDistribution.merge(key, value, Integer::sum));

        // Calculate day distribution
        checkIns.stream()
            .map(c -> c.getDateTime().getDayOfWeek().toString())
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.summingInt(_ -> 1)
            ))
            .forEach((key, value) -> dayDistribution.merge(key, value, Integer::sum));

        summary.setNoOfCheckIns(noOfCheckIns);
        summary.setStreak(streak);
        summary.setTypeDistribution(typeDistribution);
        summary.setHourDistribution(hourDistribution);
        summary.setDayDistribution(dayDistribution);

        checkInProjectWeeklySummaryRepository.save(summary);
    }

    @Transactional
    public void addCheckInToSummary(long projectId, long checkInProjectId) {
        projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        var checkInProject = checkInProjectService.getCheckIn(checkInProjectId)
            .orElseThrow(CheckInProjectNotFoundException::new);

        int year = checkInProject.getDateTime().getYear();
        int weekNo = checkInProject.getDateTime().get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfYear());

        var summary = checkInProjectWeeklySummaryRepository.findByProjectIdAndYearAndWeekNoWithLock(projectId, year, weekNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project, year and week. Cannot lock and update."));

        var typeKey = Objects.requireNonNullElse(checkInProject.getType(), "unknown");
        var hourKey = String.valueOf(checkInProject.getDateTime().getHour());
        var dayKey = checkInProject.getDateTime().getDayOfWeek().toString();

        int noOfCheckIns = summary.getNoOfCheckIns() + 1;
        int typeCount = summary.getTypeDistribution()
            .getOrDefault(typeKey, 0) + 1;
        int hourCount = summary.getHourDistribution()
            .getOrDefault(hourKey, 0) + 1;
        int dayCount = summary.getDayDistribution()
            .getOrDefault(dayKey, 0) + 1;

        summary.setNoOfCheckIns(noOfCheckIns);
        summary.getTypeDistribution().put(typeKey, typeCount);
        summary.getHourDistribution().put(hourKey, hourCount);
        summary.getDayDistribution().put(dayKey, dayCount);

        int streak = StreakUtility.calculateMaxStreakFromDayDistribution(summary.getDayDistribution());
        summary.setStreak(streak);

        checkInProjectWeeklySummaryRepository.save(summary);
    }

    public void initSummary(long projectId) {
        var project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        int year = LocalDate.now().getYear();

        List<CheckInProjectWeeklySummary> summaries = new ArrayList<>();
        var currentDayOfYear = LocalDate.now().withDayOfYear(1);
        var lastDayOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        while (currentDayOfYear.isBefore(lastDayOfYear)) {
            var weekNo = currentDayOfYear.get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfYear());
            var startDate = currentDayOfYear.with(DayOfWeek.MONDAY);
            var endDate = startDate.plusDays(6);

            var summary = new CheckInProjectWeeklySummary(
                year,
                weekNo,
                startDate,
                endDate,
                0,
                0,
                project
            );

            summaries.add(summary);

            currentDayOfYear = currentDayOfYear.plusWeeks(1);
        }

        checkInProjectWeeklySummaryRepository.saveAll(summaries);
    }

    public void initSummaries() {
        var projects = projectService.getAllProjects();

        for (Project project : projects) {
            initSummary(project.getId());
        }
    }
}
