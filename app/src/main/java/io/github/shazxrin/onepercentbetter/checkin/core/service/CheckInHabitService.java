package io.github.shazxrin.onepercentbetter.checkin.core.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInHabitNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInHabit;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInHabitRepository;
import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import io.github.shazxrin.onepercentbetter.habit.service.HabitService;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void addCheckIn(long habitId, LocalDate date, int amount, String notes) {
        Habit habit = habitService.getHabitById(habitId);

        CheckInHabit checkInHabit = new CheckInHabit(date, amount, notes, habit);
        checkInHabitRepository.save(checkInHabit);
    }

    @Transactional
    public void removeCheckIn(long checkInId) {
        var isCheckInHabitExists = checkInHabitRepository.existsById(checkInId);
        if (!isCheckInHabitExists) {
            throw new CheckInHabitNotFoundException("Check in habit not found.");
        }

        checkInHabitRepository.deleteById(checkInId);
    }

    public void updateCheckIn(long checkInId, LocalDate date, int amount, String notes) {
        var checkInHabit = checkInHabitRepository.findById(checkInId)
            .orElseThrow(() -> new CheckInHabitNotFoundException("Check in habit not found."));

        checkInHabit.setDate(date);
        checkInHabit.setAmount(amount);
        checkInHabit.setNotes(notes);

        checkInHabitRepository.save(checkInHabit);
    }
}
