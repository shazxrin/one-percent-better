package io.github.shazxrin.onepercentbetter.checkin.dto;

import java.time.LocalDate;

public record AddCheckInHabit(
    LocalDate date,
    int amount,
    String notes
) { }
