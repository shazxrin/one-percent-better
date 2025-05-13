package io.github.shazxrin.onepercentbetter.dto.checkin;

import io.github.shazxrin.onepercentbetter.model.CheckIn;

public record TodaysCheckInDto(int count, int streak) {
    public static TodaysCheckInDto fromCheckIn(CheckIn checkIn) {
        return new TodaysCheckInDto(
            checkIn.getCount(),
            checkIn.getStreak()
        );
    }
}