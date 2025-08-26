package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectMonthlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.repository.CheckInProjectMonthlySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.utility.StreakUtility;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Observed
@Service
public class CheckInProjectMonthlySummaryService {
    private final CheckInProjectMonthlySummaryRepository repository;
    private final ProjectService projectService;
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectMonthlySummaryService(
        CheckInProjectMonthlySummaryRepository repository,
        ProjectService projectService,
        CheckInProjectService checkInProjectService
    ) {
        this.repository = repository;
        this.projectService = projectService;
        this.checkInProjectService = checkInProjectService;
    }

    public CheckInProjectMonthlySummary getSummary(long projectId, int year, int monthNo) {
        projectService.getProjectById(projectId).orElseThrow(ProjectNotFoundException::new);
        return repository.findByProjectIdAndYearAndMonthNo(projectId, year, monthNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project, year and month."));
    }

    @Transactional
    public void calculateSummaryForMonth(long projectId, int year, int monthNo) {
        projectService.getProjectById(projectId).orElseThrow(ProjectNotFoundException::new);

        var summary = repository.findByProjectIdAndYearAndMonthNoWithLock(projectId, year, monthNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project, year and month."));

        Map<String, Integer> typeDistribution = summary.getTypeDistribution();
        Map<String, Integer> hourDistribution = summary.getHourDistribution();
        Map<String, Integer> dateDistribution = summary.getDateDistribution();

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
            .map(c -> String.valueOf(c.getDateTime().getDayOfMonth()))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(_ -> 1)))
            .forEach((k, v) -> dateDistribution.merge(k, v, Integer::sum));

        summary.setNoOfCheckIns(noOfCheckIns);
        summary.setStreak(streak);
        summary.setTypeDistribution(typeDistribution);
        summary.setHourDistribution(hourDistribution);
        summary.setDateDistribution(dateDistribution);

        repository.save(summary);
    }

    @Transactional
    public void addCheckInToSummary(long projectId, long checkInProjectId) {
        projectService.getProjectById(projectId).orElseThrow(ProjectNotFoundException::new);
        var checkIn = checkInProjectService.getCheckIn(checkInProjectId)
            .orElseThrow(CheckInProjectNotFoundException::new);

        int year = checkIn.getDateTime().getYear();
        int monthNo = checkIn.getDateTime().getMonthValue();

        var summary = repository.findByProjectIdAndYearAndMonthNoWithLock(projectId, year, monthNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given project, year and month. Cannot lock and update."));

        var typeKey = Objects.requireNonNullElse(checkIn.getType(), "unknown");
        var hourKey = String.valueOf(checkIn.getDateTime().getHour());
        var dateKey = String.valueOf(checkIn.getDateTime().getDayOfMonth());

        summary.setNoOfCheckIns(summary.getNoOfCheckIns() + 1);
        summary.getTypeDistribution().put(typeKey, summary.getTypeDistribution().getOrDefault(typeKey, 0) + 1);
        summary.getHourDistribution().put(hourKey, summary.getHourDistribution().getOrDefault(hourKey, 0) + 1);
        summary.getDateDistribution().put(dateKey, summary.getDateDistribution().getOrDefault(dateKey, 0) + 1);

        int streak = StreakUtility.calculateMaxStreakFromDayDistribution(summary.getDateDistribution());
        summary.setStreak(streak);

        repository.save(summary);
    }

    public void initSummary(long projectId) {
        Project project = projectService.getProjectById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        int year = LocalDate.now().getYear();
        List<CheckInProjectMonthlySummary> summaries = new ArrayList<>();

        for (int monthNo = 1; monthNo <= 12; monthNo++) {
            YearMonth yearMonth = YearMonth.of(year, monthNo);
            LocalDate start = yearMonth.atDay(1);
            LocalDate end = yearMonth.atEndOfMonth();

            var summary = new CheckInProjectMonthlySummary(year, monthNo, start, end, 0, 0, project);
            summaries.add(summary);
        }

        repository.saveAll(summaries);
    }

    public void initSummaries() {
        var projects = projectService.getAllProjects();
        for (Project project : projects) {
            initSummary(project.getId());
        }
    }
}
