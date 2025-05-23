package io.github.shazxrin.onepercentbetter.checkin.dto;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;

public record TodaysCheckIn(int count, int streak) {
    public static TodaysCheckIn fromCheckIn(CheckIn checkIn) {
        return new TodaysCheckIn(
            checkIn.getCount(),
            checkIn.getStreak()
        );
    }
}