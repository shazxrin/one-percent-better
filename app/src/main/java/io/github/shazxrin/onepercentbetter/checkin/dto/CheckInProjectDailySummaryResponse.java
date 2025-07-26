package io.github.shazxrin.onepercentbetter.checkin.dto;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectDailySummary;
import java.time.LocalDate;

public record CheckInProjectDailySummaryResponse(
    long id,
    long projectId,
    LocalDate date,
    int noOfCheckIns,
    int streak
) {
    public static CheckInProjectDailySummaryResponse from(CheckInProjectDailySummary summary) {
        return new CheckInProjectDailySummaryResponse(
            summary.getId(),
            summary.getProject().getId(),
            summary.getDate(),
            summary.getNoOfCheckIns(),
            summary.getStreak()
        );
    }
}
