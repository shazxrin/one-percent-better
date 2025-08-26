package io.github.shazxrin.onepercentbetter.checkin.summary.daily.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.daily.model.CheckInProjectAggregateDailySummary;
import java.util.Map;

public record CheckInProjectAggregateDailySummaryResponse(
    int noOfCheckIns,
    int streak,
    Map<String, Integer> typeDistribution,
    Map<String, Integer> hourDistribution,
    Map<String, Integer> projectDistribution
) {
    public static CheckInProjectAggregateDailySummaryResponse from(CheckInProjectAggregateDailySummary summary) {
        return new CheckInProjectAggregateDailySummaryResponse(
            summary.getNoOfCheckIns(),
            summary.getStreak(),
            summary.getTypeDistribution(),
            summary.getHourDistribution(),
            summary.getProjectDistribution()
        );
    }
}
