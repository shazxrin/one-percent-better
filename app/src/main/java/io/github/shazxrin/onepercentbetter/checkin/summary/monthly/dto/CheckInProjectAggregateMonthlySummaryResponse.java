package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectAggregateMonthlySummary;
import java.time.LocalDate;
import java.util.Map;

public record CheckInProjectAggregateMonthlySummaryResponse(
    LocalDate startDate,
    LocalDate endDate,
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution,
    Map<String, Integer> projectDistribution,
    Map<String, Integer> dateDistribution
) {
    public static CheckInProjectAggregateMonthlySummaryResponse from(CheckInProjectAggregateMonthlySummary summary) {
        return new CheckInProjectAggregateMonthlySummaryResponse(
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