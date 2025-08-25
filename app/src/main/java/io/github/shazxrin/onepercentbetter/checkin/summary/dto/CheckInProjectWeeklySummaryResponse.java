package io.github.shazxrin.onepercentbetter.checkin.summary.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectWeeklySummary;
import java.time.LocalDate;
import java.util.Map;

public record CheckInProjectWeeklySummaryResponse(
    LocalDate startDate,
    LocalDate endDate,
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution,
    Map<String, Integer> dayDistribution
) {
    public static CheckInProjectWeeklySummaryResponse from(CheckInProjectWeeklySummary summary) {
        return new CheckInProjectWeeklySummaryResponse(
            summary.getStartDate(),
            summary.getEndDate(),
            summary.getNoOfCheckIns(),
            summary.getStreak(),
            summary.getTypeDistribution(),
            summary.getHourDistribution(),
            summary.getDayDistribution()
        );
    }
}