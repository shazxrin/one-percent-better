package io.github.shazxrin.onepercentbetter.checkin.summary.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import java.util.Map;

public record CheckInProjectAggregateDailySummaryResponse(
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution
) {
    public static CheckInProjectAggregateDailySummaryResponse from(CheckInProjectAggregateDailySummary summary) {
        return new CheckInProjectAggregateDailySummaryResponse(
            summary.getNoOfCheckIns(),
            summary.getStreak(),
            summary.getTypeDistribution(),
            summary.getHourDistribution()
        );
    }
}
