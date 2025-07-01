package io.github.shazxrin.onepercentbetter.checkin.dto;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import java.time.LocalDateTime;

public record TodaysCheckIn(int count, int streak, LocalDateTime updatedAt) {
    public static TodaysCheckIn fromCheckIn(CheckIn checkIn) {
        return new TodaysCheckIn(
            checkIn.getCount(),
            checkIn.getStreak(),
            checkIn.getUpdatedAt()
        );
    }
}