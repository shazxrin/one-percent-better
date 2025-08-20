package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectAggregateDailySummaryRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckInProjectAggregateDailySummaryService {
    private final CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectAggregateDailySummaryService(
        CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository,
        CheckInProjectService checkInProjectService
    ) {
        this.checkInProjectAggregateDailySummaryRepository = checkInProjectAggregateDailySummaryRepository;
        this.checkInProjectService = checkInProjectService;
    }

    public CheckInProjectAggregateDailySummary getAggregateSummary(LocalDate date) {
        var aggregateSummaryOpt = checkInProjectAggregateDailySummaryRepository.findByDate(date);
        return aggregateSummaryOpt
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the given date."));
    }

    @Transactional
    public void calculateAggregateSummary(LocalDate date, boolean withCount) {
        LocalDate previousDate = date.minusDays(1);

        var previousDateAggregateSummary = checkInProjectAggregateDailySummaryRepository
            .findByDate(previousDate)
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the previous date."));
        var currentDateAggregateSummary = checkInProjectAggregateDailySummaryRepository
            .findByDateWithLock(date)
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the given date."));

        // If with count enabled, the check ins for the date will be fetched
        // Else it will use the existing count
        int noOfCheckIns = currentDateAggregateSummary.getNoOfCheckIns();
        Map<String, Integer> typeDistribution = currentDateAggregateSummary.getTypeDistribution();
        Map<String, Integer> hourDistribution = currentDateAggregateSummary.getHourDistribution();
        Map<String, Integer> projectDistribution = currentDateAggregateSummary.getProjectDistribution();
        if (withCount) {
            List<CheckInProject> checkIns = checkInProjectService.getAllCheckIns(date);

            // Calculate check in count
            noOfCheckIns = checkIns.size();

            // Calculate type distribution
            typeDistribution = checkIns.stream()
                .map(c -> Objects.requireNonNullElse(c.getType(), "unknown"))
                .collect(Collectors.groupingBy(
                    Function.identity(),
                    Collectors.summingInt(_ -> 1)
                ));

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
        }

        int currentStreak = 0;
        if (noOfCheckIns > 0) {
            currentStreak = previousDateAggregateSummary.getStreak() + 1;
        }

        currentDateAggregateSummary.setNoOfCheckIns(noOfCheckIns);
        currentDateAggregateSummary.setStreak(currentStreak);
        currentDateAggregateSummary.setTypeDistribution(typeDistribution);
        currentDateAggregateSummary.setHourDistribution(hourDistribution);
        currentDateAggregateSummary.setProjectDistribution(projectDistribution);

        checkInProjectAggregateDailySummaryRepository.save(currentDateAggregateSummary);
    }

    @Transactional
    public void addCheckInToAggregateSummary(long checkInProjectId) {
        var checkInProject = checkInProjectService.getCheckIn(checkInProjectId)
            .orElseThrow(CheckInProjectNotFoundException::new);

        LocalDate date = checkInProject.getDateTime().toLocalDate();
        LocalDate previousDate = date.minusDays(1);

        var previousDateAggregateSummary = checkInProjectAggregateDailySummaryRepository
            .findByDate(previousDate)
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the previous date."));
        var currentDateAggregateSummary = checkInProjectAggregateDailySummaryRepository
            .findByDateWithLock(date)
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the given date. Cannot lock and update."));

        int noOfCheckIns = currentDateAggregateSummary.getNoOfCheckIns() + 1;
        int currentStreak = previousDateAggregateSummary.getStreak() + 1;
        int typeCount = currentDateAggregateSummary.getTypeDistribution()
            .getOrDefault(Objects.requireNonNullElse(checkInProject.getType(), "unknown"), 0) + 1;
        int hourCount = currentDateAggregateSummary.getHourDistribution()
            .getOrDefault(String.valueOf(checkInProject.getDateTime().getHour()), 0) + 1;
        int projectCount = currentDateAggregateSummary.getProjectDistribution()
            .getOrDefault(checkInProject.getProject().getName(), 0) + 1;

        currentDateAggregateSummary.setNoOfCheckIns(noOfCheckIns);
        currentDateAggregateSummary.setStreak(currentStreak);
        currentDateAggregateSummary.getTypeDistribution()
            .put(Objects.requireNonNullElse(checkInProject.getType(), "unknown"), typeCount);
        currentDateAggregateSummary.getHourDistribution()
            .put(String.valueOf(checkInProject.getDateTime().getHour()), hourCount);
        currentDateAggregateSummary.getProjectDistribution()
            .put(checkInProject.getProject().getName(), projectCount);

        checkInProjectAggregateDailySummaryRepository.save(currentDateAggregateSummary);

        // Recalculate dates after the current date (if any)
        // Streak may have changed from the current date onwards
        var today = LocalDate.now();
        var currentDate = date.plusDays(1);
        while (!currentDate.isAfter(today)) {
            calculateAggregateSummary(currentDate, false);

            currentDate = currentDate.plusDays(1);
        }
    }

    public void initAggregateSummaries() {
        var now = LocalDate.now();

        List<CheckInProjectAggregateDailySummary> summaries = new ArrayList<>();
        for (int i = 1; i <= now.lengthOfYear(); i++) {
            summaries.add(new CheckInProjectAggregateDailySummary(now.withDayOfYear(i), 0, 0));
        }

        checkInProjectAggregateDailySummaryRepository.saveAll(summaries);
    }
}
