package io.github.shazxrin.onepercentbetter.checkin.core.dto;

import java.time.LocalDate;

public record CheckInHabitAddRequest(
    LocalDate date,
    int amount,
    String notes
) {
}
