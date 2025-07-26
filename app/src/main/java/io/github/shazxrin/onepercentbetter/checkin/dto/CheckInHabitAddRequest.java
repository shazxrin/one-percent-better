package io.github.shazxrin.onepercentbetter.checkin.dto;

import java.time.LocalDate;

public record CheckInHabitAddRequest(
    LocalDate date,
    int amount,
    String notes
) {
}
