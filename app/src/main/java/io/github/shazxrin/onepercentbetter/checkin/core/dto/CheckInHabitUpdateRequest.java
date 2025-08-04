package io.github.shazxrin.onepercentbetter.checkin.core.dto;

import java.time.LocalDate;

public record CheckInHabitUpdateRequest(
    LocalDate date,
    int amount,
    String notes
) {
}
