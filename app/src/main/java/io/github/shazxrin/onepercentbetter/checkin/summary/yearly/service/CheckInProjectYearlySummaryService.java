package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.utility.StreakUtility;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectYearlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.repository.CheckInProjectYearlySummaryRepository;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Observed
@Service
public class CheckInProjectYearlySummaryService {
    private final CheckInProjectYearlySummaryRepository repository;
    private final ProjectService projectService;
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectYearlySummaryService(
        CheckInProjectYearlySummaryRepository repository,
        ProjectService projectService,
        CheckInProjectService checkInProjectService
    ) {
        this.repository = repository;
        this.projectService = projectService;
        this.checkInProjectService = checkInProjectService;
    }

    public CheckInProjectYearlySummary getSummary(long projectId, int year) {
        projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        return repository.findByProjectIdAndYear(projectId, year)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project and year."));
    }

    @Transactional
    public void calculateSummaryForYear(long projectId, int year) {
        projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        var summary = repository.findByProjectIdAndYearWithLock(projectId, year)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project and year."));

        Map<String, Integer> typeDistribution = summary.getTypeDistribution();
        Map<String, Integer> hourDistribution = summary.getHourDistribution();
        Map<String, Integer> dayDistribution = summary.getDayDistribution();

        LocalDate startDate = summary.getStartDate();
        LocalDate endDate = summary.getEndDate();
        List<CheckInProject> checkIns = checkInProjectService.getAllCheckInsByProjectBetween(projectId, startDate, endDate);

        int noOfCheckIns = checkIns.size();
        int streak = StreakUtility.calculateMaxStreakFromCheckIns(checkIns);

        checkIns.stream()
            .map(c -> Objects.requireNonNullElse(c.getType(), "unknown"))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(_ -> 1)))
            .forEach((k, v) -> typeDistribution.merge(k, v, Integer::sum));

        checkIns.stream()
            .map(c -> String.valueOf(c.getDateTime().getHour()))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(_ -> 1)))
            .forEach((k, v) -> hourDistribution.merge(k, v, Integer::sum));

        checkIns.stream()
            .map(c -> String.valueOf(c.getDateTime().getDayOfYear()))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(_ -> 1)))
            .forEach((k, v) -> dayDistribution.merge(k, v, Integer::sum));

        summary.setNoOfCheckIns(noOfCheckIns);
        summary.setStreak(streak);
        summary.setTypeDistribution(typeDistribution);
        summary.setHourDistribution(hourDistribution);
        summary.setDayDistribution(dayDistribution);

        repository.save(summary);
    }

    @Transactional
    public void addCheckInToSummary(long projectId, long checkInProjectId) {
        projectService.getProjectById(projectId).orElseThrow(ProjectNotFoundException::new);
        var checkIn = checkInProjectService.getCheckIn(checkInProjectId)
            .orElseThrow(CheckInProjectNotFoundException::new);

        int year = checkIn.getDateTime().getYear();

        var summary = repository.findByProjectIdAndYearWithLock(projectId, year)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project and year. Cannot lock and update."));

        var typeKey = Objects.requireNonNullElse(checkIn.getType(), "unknown");
        var hourKey = String.valueOf(checkIn.getDateTime().getHour());
        var dayKey = String.valueOf(checkIn.getDateTime().getDayOfYear());

        summary.setNoOfCheckIns(summary.getNoOfCheckIns() + 1);
        summary.getTypeDistribution().put(typeKey, summary.getTypeDistribution().getOrDefault(typeKey, 0) + 1);
        summary.getHourDistribution().put(hourKey, summary.getHourDistribution().getOrDefault(hourKey, 0) + 1);
        summary.getDayDistribution().put(dayKey, summary.getDayDistribution().getOrDefault(dayKey, 0) + 1);

        int streak = StreakUtility.calculateMaxStreakFromDateDistribution(summary.getDayDistribution());
        summary.setStreak(streak);

        repository.save(summary);
    }

    public void initSummary(long projectId) {
        Project project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        Year year = Year.of(LocalDate.now().getYear());
        LocalDate start = year.atDay(1);
        LocalDate end = year.atDay(year.length());

        var summary = new CheckInProjectYearlySummary(year.getValue(), start, end, 0, 0, project);

        repository.save(summary);
    }

    public void initSummaries() {
        var projects = projectService.getAllProjects();
        for (Project project : projects) {
            initSummary(project.getId());
        }
    }
}