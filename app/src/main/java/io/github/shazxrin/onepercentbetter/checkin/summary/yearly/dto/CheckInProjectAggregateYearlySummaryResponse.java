package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectAggregateYearlySummary;
import java.time.LocalDate;
import java.util.Map;

public record CheckInProjectAggregateYearlySummaryResponse(
    LocalDate startDate,
    LocalDate endDate,
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution,
    Map<String, Integer> projectDistribution,
    Map<String, Integer> dayDistribution
) {
    public static CheckInProjectAggregateYearlySummaryResponse from(CheckInProjectAggregateYearlySummary summary) {
        return new CheckInProjectAggregateYearlySummaryResponse(
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