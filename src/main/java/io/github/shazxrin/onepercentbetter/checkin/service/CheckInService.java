package io.github.shazxrin.onepercentbetter.checkin.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import java.time.LocalDate;

public interface CheckInService {
    void checkInToday();

    CheckIn getTodaysCheckIn();

    void checkInBootstrap(LocalDate bootstrapDate);
}
