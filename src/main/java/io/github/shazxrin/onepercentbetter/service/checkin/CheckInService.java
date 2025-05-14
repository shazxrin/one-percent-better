package io.github.shazxrin.onepercentbetter.service.checkin;

import io.github.shazxrin.onepercentbetter.model.CheckIn;

public interface CheckInService {
    void checkInToday();

    CheckIn getTodaysCheckIn();
}
