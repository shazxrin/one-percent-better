package io.github.shazxrin.onepercentbetter.coach.service;

import io.github.shazxrin.onepercentbetter.coach.model.CoachReminder;

public interface CoachService {
    CoachReminder promptReminder(int commitsToday, int streakToday);
}
