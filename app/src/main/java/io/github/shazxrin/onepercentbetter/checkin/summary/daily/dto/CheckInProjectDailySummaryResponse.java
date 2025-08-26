package io.github.shazxrin.onepercentbetter.checkin.summary.daily.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.daily.model.CheckInProjectDailySummary;
import java.util.Map;

public record CheckInProjectDailySummaryResponse(
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution
) {
    public static CheckInProjectDailySummaryResponse from(CheckInProjectDailySummary summary) {
        return new CheckInProjectDailySummaryResponse(
            summary.getNoOfCheckIns(),
            summary.getStreak(),
            summary.getTypeDistribution(),
            summary.getHourDistribution()
        );
    }
}
