package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectYearlySummary;

import java.time.LocalDate;
import java.util.Map;

public record CheckInProjectYearlySummaryResponse(
    LocalDate startDate,
    LocalDate endDate,
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution,
    Map<String, Integer> dayDistribution
) {
    public static CheckInProjectYearlySummaryResponse from(CheckInProjectYearlySummary summary) {
        return new CheckInProjectYearlySummaryResponse(
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