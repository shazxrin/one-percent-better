package io.github.shazxrin.onepercentbetter.checkin.dto;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectDailySummary;

public record CheckInProjectDailySummaryResponse(
    int noOfCheckIns,
    int streak
) {
    public static CheckInProjectDailySummaryResponse from(CheckInProjectDailySummary summary) {
        return new CheckInProjectDailySummaryResponse(
            summary.getNoOfCheckIns(),
            summary.getStreak()
        );
    }
}
