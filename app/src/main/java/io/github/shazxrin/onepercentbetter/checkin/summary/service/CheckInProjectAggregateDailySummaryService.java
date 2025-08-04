package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectAggregateDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class CheckInProjectAggregateDailySummaryService {
    private final CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;
    private final CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    public CheckInProjectAggregateDailySummaryService(
        CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository,
        CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository
    ) {
        this.checkInProjectAggregateDailySummaryRepository = checkInProjectAggregateDailySummaryRepository;
        this.checkInProjectDailySummaryRepository = checkInProjectDailySummaryRepository;
    }

    public void initAggregateSummary(LocalDate date) {
        var newSummary = new CheckInProjectAggregateDailySummary(
            date,
            0,
            0
        );

        checkInProjectAggregateDailySummaryRepository.save(newSummary);
    }

    public void calculateAggregateSummary(LocalDate date) {
        LocalDate previousDate = date.minusDays(1);

        var previousDateAggregateSummaryOpt = checkInProjectAggregateDailySummaryRepository
            .findByDate(previousDate);
        var currentDateAggregateSummary = checkInProjectAggregateDailySummaryRepository
            .findByDate(date)
            .orElse(new CheckInProjectAggregateDailySummary(date, 0, 0));

        int currentStreak = previousDateAggregateSummaryOpt
            .map(CheckInProjectAggregateDailySummary::getStreak)
            .orElse(0);

        int totalCommits = checkInProjectDailySummaryRepository
            .findAllByDate(date)
            .stream()
            .map(CheckInProjectDailySummary::getNoOfCheckIns)
            .reduce(0, Integer::sum);
        if (totalCommits > 0) {
            currentStreak++;
        } else {
            currentStreak = 0;
        }

        currentDateAggregateSummary.setNoOfCheckIns(totalCommits);
        currentDateAggregateSummary.setStreak(currentStreak);

        checkInProjectAggregateDailySummaryRepository.save(currentDateAggregateSummary);
    }

    public CheckInProjectAggregateDailySummary getAggregateSummary(LocalDate date) {
        var aggregateSummaryOpt = checkInProjectAggregateDailySummaryRepository.findByDate(date);
        if (aggregateSummaryOpt.isPresent()) {
            return aggregateSummaryOpt.get();
        }

        var newSummary = new CheckInProjectAggregateDailySummary(
            date,
            0,
            0
        );
        return checkInProjectAggregateDailySummaryRepository.save(newSummary);
    }
}
