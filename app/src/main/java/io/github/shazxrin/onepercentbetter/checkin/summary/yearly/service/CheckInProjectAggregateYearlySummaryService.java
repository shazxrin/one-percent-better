package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectAggregateYearlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.repository.CheckInProjectAggregateYearlySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.utility.StreakUtility;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Observed
@Service
public class CheckInProjectAggregateYearlySummaryService {
    private final CheckInProjectAggregateYearlySummaryRepository checkInProjectAggregateYearlySummaryRepository;
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectAggregateYearlySummaryService(
        CheckInProjectAggregateYearlySummaryRepository checkInProjectAggregateYearlySummaryRepository,
        CheckInProjectService checkInProjectService
    ) {
        this.checkInProjectAggregateYearlySummaryRepository = checkInProjectAggregateYearlySummaryRepository;
        this.checkInProjectService = checkInProjectService;
    }

    public CheckInProjectAggregateYearlySummary getAggregateSummary(int year) {
        var aggregateSummaryOpt = checkInProjectAggregateYearlySummaryRepository.findByYear(year);
        return aggregateSummaryOpt
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the given year."));
    }

    @Transactional
    public void calculateAggregateSummaryForYear(int year) {
        var summary = checkInProjectAggregateYearlySummaryRepository.findByYearWithLock(year)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given year. Cannot lock and update."));

        int noOfCheckIns;
        int streak;
        Map<String, Integer> typeDistribution = summary.getTypeDistribution();
        Map<String, Integer> hourDistribution = summary.getHourDistribution();
        Map<String, Integer> projectDistribution = summary.getProjectDistribution();
        Map<String, Integer> dayDistribution = summary.getDayDistribution();

        var startDate = summary.getStartDate();
        var endDate = summary.getEndDate();
        List<CheckInProject> checkIns = checkInProjectService.getAllCheckInsBetween(startDate, endDate);

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

        // Calculate project distribution
        checkIns.stream()
            .map(c -> c.getProject().getName())
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.summingInt(_ -> 1)
            ))
            .forEach((key, value) -> projectDistribution.merge(key, value, Integer::sum));

        // Calculate day distribution
        checkIns.stream()
            .map(c -> String.valueOf(c.getDateTime().getDayOfYear()))
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.summingInt(_ -> 1)
            ))
            .forEach((key, value) -> dayDistribution.merge(key, value, Integer::sum));

        summary.setNoOfCheckIns(noOfCheckIns);
        summary.setStreak(streak);
        summary.setTypeDistribution(typeDistribution);
        summary.setHourDistribution(hourDistribution);
        summary.setProjectDistribution(projectDistribution);
        summary.setDayDistribution(dayDistribution);

        checkInProjectAggregateYearlySummaryRepository.save(summary);
    }

    @Transactional
    public void addCheckInToAggregateSummary(long checkInProjectId) {
        var checkInProject = checkInProjectService.getCheckIn(checkInProjectId)
            .orElseThrow(CheckInProjectNotFoundException::new);

        int year = checkInProject.getDateTime().getYear();

        var summary = checkInProjectAggregateYearlySummaryRepository.findByYearWithLock(year)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given year. Cannot update."));

        var typeKey = Objects.requireNonNullElse(checkInProject.getType(), "unknown");
        var hourKey = String.valueOf(checkInProject.getDateTime().getHour());
        var projectKey = checkInProject.getProject().getName();
        var dayKey = String.valueOf(checkInProject.getDateTime().getDayOfYear());

        int noOfCheckIns = summary.getNoOfCheckIns() + 1;
        int typeCount = summary.getTypeDistribution()
            .getOrDefault(typeKey, 0) + 1;
        int hourCount = summary.getHourDistribution()
            .getOrDefault(hourKey, 0) + 1;
        int projectCount = summary.getProjectDistribution()
            .getOrDefault(projectKey, 0) + 1;
        int dayCount = summary.getDayDistribution()
            .getOrDefault(dayKey, 0) + 1;

        summary.setNoOfCheckIns(noOfCheckIns);
        summary.getTypeDistribution().put(typeKey, typeCount);
        summary.getHourDistribution().put(hourKey, hourCount);
        summary.getProjectDistribution().put(projectKey, projectCount);
        summary.getDayDistribution().put(dayKey, dayCount);

        // For yearly, we'll need to recalculate the streak from checkIns
        // since the dayDistribution doesn't provide enough detail for streak calculation
        List<CheckInProject> checkIns = checkInProjectService.getAllCheckInsBetween(summary.getStartDate(), summary.getEndDate());
        int streak = StreakUtility.calculateMaxStreakFromCheckIns(checkIns);
        summary.setStreak(streak);

        checkInProjectAggregateYearlySummaryRepository.save(summary);
    }

    public void initAggregateSummary(int year) {
        var startDate = LocalDate.of(year, Month.JANUARY, 1);
        var endDate = LocalDate.of(year, Month.DECEMBER, 31);

        var summary = new CheckInProjectAggregateYearlySummary(
            year,
            startDate,
            endDate,
            0,
            0
        );

        checkInProjectAggregateYearlySummaryRepository.save(summary);
    }
}