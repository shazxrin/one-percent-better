package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectAggregateWeeklySummary;
import java.time.LocalDate;
import java.util.Map;

public record CheckInProjectAggregateWeeklySummaryResponse(
    LocalDate startDate,
    LocalDate endDate,
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution,
    Map<String, Integer> projectDistribution,
    Map<String, Integer> dayDistribution
) {
    public static CheckInProjectAggregateWeeklySummaryResponse from(CheckInProjectAggregateWeeklySummary summary) {
        return new CheckInProjectAggregateWeeklySummaryResponse(
            summary.getStartDate(),
            summary.getEndDate(),
            summary.getNoOfCheckIns(),
            summary.getStreak(),
            summary.getTypeDistribution(),
            summary.getHourDistribution(),
            summary.getProjectDistribution(),
            summary.getDayDistribution()
        );
    }
}