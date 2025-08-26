package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectAggregateMonthlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.repository.CheckInProjectAggregateMonthlySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.utility.StreakUtility;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import java.time.YearMonth;
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
public class CheckInProjectAggregateMonthlySummaryService {
    private final CheckInProjectAggregateMonthlySummaryRepository checkInProjectAggregateMonthlySummaryRepository;
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectAggregateMonthlySummaryService(
        CheckInProjectAggregateMonthlySummaryRepository checkInProjectAggregateMonthlySummaryRepository,
        CheckInProjectService checkInProjectService
    ) {
        this.checkInProjectAggregateMonthlySummaryRepository = checkInProjectAggregateMonthlySummaryRepository;
        this.checkInProjectService = checkInProjectService;
    }

    public CheckInProjectAggregateMonthlySummary getAggregateSummary(int year, int monthNo) {
        var aggregateSummaryOpt = checkInProjectAggregateMonthlySummaryRepository.findByYearAndMonthNo(year, monthNo);
        return aggregateSummaryOpt
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the given year and monthNo."));
    }

    @Transactional
    public void calculateAggregateSummaryForMonth(int year, int monthNo) {
        var summary = checkInProjectAggregateMonthlySummaryRepository.findByYearAndMonthNoWithLock(year, monthNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given year and monthNo. Cannot lock and update."));

        int noOfCheckIns;
        int streak;
        Map<String, Integer> typeDistribution = summary.getTypeDistribution();
        Map<String, Integer> hourDistribution = summary.getHourDistribution();
        Map<String, Integer> projectDistribution = summary.getProjectDistribution();
        Map<String, Integer> dateDistribution = summary.getDateDistribution();

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

        // Calculate date distribution
        checkIns.stream()
            .map(c -> String.valueOf(c.getDateTime().getDayOfMonth()))
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.summingInt(_ -> 1)
            ))
            .forEach((key, value) -> dateDistribution.merge(key, value, Integer::sum));

        summary.setNoOfCheckIns(noOfCheckIns);
        summary.setStreak(streak);
        summary.setTypeDistribution(typeDistribution);
        summary.setHourDistribution(hourDistribution);
        summary.setProjectDistribution(projectDistribution);
        summary.setDateDistribution(dateDistribution);

        checkInProjectAggregateMonthlySummaryRepository.save(summary);
    }

    @Transactional
    public void addCheckInToAggregateSummary(long checkInProjectId) {
        var checkInProject = checkInProjectService.getCheckIn(checkInProjectId)
            .orElseThrow(CheckInProjectNotFoundException::new);

        int year = checkInProject.getDateTime().getYear();
        int monthNo = checkInProject.getDateTime().getMonthValue();

        var summary = checkInProjectAggregateMonthlySummaryRepository.findByYearAndMonthNo(year, monthNo)
            .orElseThrow(() -> new IllegalStateException("No summary found for the given year and monthNo. Cannot update."));

        var typeKey = Objects.requireNonNullElse(checkInProject.getType(), "unknown");
        var hourKey = String.valueOf(checkInProject.getDateTime().getHour());
        var projectKey = checkInProject.getProject().getName();
        var dateKey = String.valueOf(checkInProject.getDateTime().getDayOfMonth());

        int noOfCheckIns = summary.getNoOfCheckIns() + 1;
        int typeCount = summary.getTypeDistribution()
            .getOrDefault(typeKey, 0) + 1;
        int hourCount = summary.getHourDistribution()
            .getOrDefault(hourKey, 0) + 1;
        int projectCount = summary.getProjectDistribution()
            .getOrDefault(projectKey, 0) + 1;
        int dateCount = summary.getDateDistribution()
            .getOrDefault(dateKey, 0) + 1;

        summary.setNoOfCheckIns(noOfCheckIns);
        summary.getTypeDistribution().put(typeKey, typeCount);
        summary.getHourDistribution().put(hourKey, hourCount);
        summary.getProjectDistribution().put(projectKey, projectCount);
        summary.getDateDistribution().put(dateKey, dateCount);

        int streak = StreakUtility.calculateMaxStreakFromDateDistribution(summary.getDateDistribution());
        summary.setStreak(streak);

        checkInProjectAggregateMonthlySummaryRepository.save(summary);
    }

    public void initAggregateSummaries() {
        int year = LocalDate.now().getYear();

        List<CheckInProjectAggregateMonthlySummary> summaries = new ArrayList<>();
        for (int monthNo = 1; monthNo <= 12; monthNo++) {
            YearMonth yearMonth = YearMonth.of(year, monthNo);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();

            var summary = new CheckInProjectAggregateMonthlySummary(
                year,
                monthNo,
                startDate,
                endDate,
                0,
                0
            );

            summaries.add(summary);
        }

        checkInProjectAggregateMonthlySummaryRepository.saveAll(summaries);
    }
}