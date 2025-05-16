package io.github.shazxrin.onepercentbetter.checkin.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;

public interface CheckInService {
    void checkInToday();

    CheckIn getTodaysCheckIn();
}
