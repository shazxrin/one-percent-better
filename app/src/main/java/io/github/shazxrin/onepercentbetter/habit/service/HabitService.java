package io.github.shazxrin.onepercentbetter.habit.service;

import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HabitService {
    void addHabit(String name, String description);

    @Transactional
    void removeHabit(Long id);

    List<Habit> getAllHabits();
}
