package io.github.shazxrin.onepercentbetter.checkin.summary.dto;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;

public record CheckInProjectAggregateDailySummaryResponse(
    int noOfCheckIns,
    int streak
) {
    public static CheckInProjectAggregateDailySummaryResponse from(CheckInProjectAggregateDailySummary summary) {
        return new CheckInProjectAggregateDailySummaryResponse(
            summary.getNoOfCheckIns(),
            summary.getStreak()
        );
    }
}
