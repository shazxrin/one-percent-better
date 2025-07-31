package io.github.shazxrin.onepercentbetter.checkin.dto;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectAggregateDailySummary;
import java.time.LocalDate;

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
