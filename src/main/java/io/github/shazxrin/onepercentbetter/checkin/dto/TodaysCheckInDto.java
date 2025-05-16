package io.github.shazxrin.onepercentbetter.checkin.dto;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;

public record TodaysCheckInDto(int count, int streak) {
    public static TodaysCheckInDto fromCheckIn(CheckIn checkIn) {
        return new TodaysCheckInDto(
            checkIn.getCount(),
            checkIn.getStreak()
        );
    }
}