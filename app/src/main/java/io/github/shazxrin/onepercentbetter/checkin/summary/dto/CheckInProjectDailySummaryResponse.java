package io.github.shazxrin.onepercentbetter.checkin.summary.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import java.util.Map;

public record CheckInProjectDailySummaryResponse(
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution
) {
    public static CheckInProjectDailySummaryResponse from(CheckInProjectDailySummary summary) {
        return new CheckInProjectDailySummaryResponse(
            summary.getNoOfCheckIns(),
            summary.getStreak(),
            summary.getTypeDistribution()
        );
    }
}
