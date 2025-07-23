package io.github.shazxrin.onepercentbetter.checkin.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInHabit;
import io.github.shazxrin.onepercentbetter.checkin.repository.CheckInHabitRepository;
import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import io.github.shazxrin.onepercentbetter.habit.service.HabitService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Observed
@Service
public class CheckInHabitService {
    private final CheckInHabitRepository checkInHabitRepository;
    private final HabitService habitService;

    public CheckInHabitService(
        CheckInHabitRepository checkInHabitRepository,
        HabitService habitService
    ) {
        this.checkInHabitRepository = checkInHabitRepository;
        this.habitService = habitService;
    }

    public void checkIn(long habitId, LocalDate date, int amount, String notes) {
        Habit habit = habitService.getHabitById(habitId);

        CheckInHabit checkInHabit = new CheckInHabit(date, amount, notes, habit);
        checkInHabitRepository.save(checkInHabit);
    }
}
