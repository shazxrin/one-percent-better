package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectAggregateDailySummaryRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckInProjectAggregateDailySummaryService {
    private final CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;
    private final CheckInProjectRepository checkInProjectRepository;

    public CheckInProjectAggregateDailySummaryService(
        CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository,
        CheckInProjectRepository checkInProjectRepository
    ) {
        this.checkInProjectAggregateDailySummaryRepository = checkInProjectAggregateDailySummaryRepository;
        this.checkInProjectRepository = checkInProjectRepository;
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
        if (withCount) {
            noOfCheckIns = checkInProjectRepository.countByDate(date);
        }

        int currentStreak = 0;
        if (noOfCheckIns > 0) {
            currentStreak = previousDateAggregateSummary.getStreak() + 1;
        }

        currentDateAggregateSummary.setNoOfCheckIns(noOfCheckIns);
        currentDateAggregateSummary.setStreak(currentStreak);

        checkInProjectAggregateDailySummaryRepository.save(currentDateAggregateSummary);
    }

    @Transactional
    public void addCheckInToAggregateSummary(LocalDate date) {
        LocalDate previousDate = date.minusDays(1);

        var previousDateAggregateSummary = checkInProjectAggregateDailySummaryRepository
            .findByDate(previousDate)
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the previous date."));
        var currentDateAggregateSummary = checkInProjectAggregateDailySummaryRepository
            .findByDateWithLock(date)
            .orElseThrow(() -> new IllegalStateException("No aggregate summary found for the given date. Cannot lock and update."));

        int noOfCheckIns = currentDateAggregateSummary.getNoOfCheckIns() + 1;
        int currentStreak = previousDateAggregateSummary.getStreak() + 1;

        currentDateAggregateSummary.setNoOfCheckIns(noOfCheckIns);
        currentDateAggregateSummary.setStreak(currentStreak);

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
