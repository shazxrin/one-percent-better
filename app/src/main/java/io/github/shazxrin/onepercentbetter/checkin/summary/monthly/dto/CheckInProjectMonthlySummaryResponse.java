package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectMonthlySummary;

import java.time.LocalDate;
import java.util.Map;

public record CheckInProjectMonthlySummaryResponse(
    LocalDate startDate,
    LocalDate endDate,
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution,
    Map<String, Integer> dateDistribution
) {
    public static CheckInProjectMonthlySummaryResponse from(CheckInProjectMonthlySummary summary) {
        return new CheckInProjectMonthlySummaryResponse(
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
