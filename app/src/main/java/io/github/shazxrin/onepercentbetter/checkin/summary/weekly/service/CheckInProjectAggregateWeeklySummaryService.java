package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectAggregateWeeklySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.repository.CheckInProjectAggregateWeeklySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.utility.StreakUtility;
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
public class CheckInProjectAggregateWeeklySummaryService {
    private final CheckInProjectAggregateWeeklySummaryRepository checkInProjectAggregateWeeklySummaryRepository;

    private final CheckInProjectService checkInProjectService;

    public CheckInProjectAggregateWeeklySummaryService(
        CheckInProjectAggregateWeeklySummaryRepository checkInProjectAggregateWeeklySummaryRepository,
        CheckInProjectService checkInProjectService
    ) {
        this.checkInProjectAggregateWeeklySummaryRepository = checkInProjectAggregateWeeklySummaryRepository;
        this.checkInProjectService = checkInProjectService;
    }

    public CheckInProjectAggregateWeeklySummary getAggregateSummary(int year, int weekNo) {
        var aggregateSummaryOpt = checkInProjectAggregateWeeklySummaryRepository.findByYearAndWeekNo(year, weekNo);
        return aggregateSummaryOpt
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the given year and weekNo."));
    }

    @Transactional
    public void calculateAggregateSummaryForWeek(int year, int weekNo) {
        var summary = checkInProjectAggregateWeeklySummaryRepository.findByYearAndWeekNoWithLock(year, weekNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given year and weekNo. Cannot lock and update."));

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
            .map(c -> String.valueOf(c.getDateTime().getDayOfWeek().getValue()))
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

        checkInProjectAggregateWeeklySummaryRepository.save(summary);
    }

    @Transactional
    public void addCheckInToAggregateSummary(long checkInProjectId) {
        var checkInProject = checkInProjectService.getCheckIn(checkInProjectId)
            .orElseThrow(CheckInProjectNotFoundException::new);

        int year = checkInProject.getDateTime().getYear();
        int weekNo = checkInProject.getDateTime().get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfYear());

        var summary = checkInProjectAggregateWeeklySummaryRepository.findByYearAndWeekNo(year, weekNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given year and weekNo. Cannot lock and update."));

        var typeKey = Objects.requireNonNullElse(checkInProject.getType(), "unknown");
        var hourKey = String.valueOf(checkInProject.getDateTime().getHour());
        var projectKey = checkInProject.getProject().getName();
        var dayKey = String.valueOf(checkInProject.getDateTime().getDayOfWeek().getValue());

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

        int streak = StreakUtility.calculateMaxStreakFromDayDistribution(summary.getDayDistribution());
        summary.setStreak(streak);

        checkInProjectAggregateWeeklySummaryRepository.save(summary);
    }

    public void initAggregateSummaries() {
        int year = LocalDate.now().getYear();

        List<CheckInProjectAggregateWeeklySummary> summaries = new ArrayList<>();
        var currentDayOfYear = LocalDate.now().withDayOfYear(1);
        var lastDayOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        while (currentDayOfYear.isBefore(lastDayOfYear)) {
            var weekNo = currentDayOfYear.get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfYear());
            var startDate = currentDayOfYear.with(DayOfWeek.MONDAY);
            var endDate = startDate.plusDays(6);

            var summary = new CheckInProjectAggregateWeeklySummary(
                year,
                weekNo,
                startDate,
                endDate,
                0,
                0
            );

            summaries.add(summary);

            currentDayOfYear = currentDayOfYear.plusWeeks(1);
        }

        checkInProjectAggregateWeeklySummaryRepository.saveAll(summaries);
    }
}
