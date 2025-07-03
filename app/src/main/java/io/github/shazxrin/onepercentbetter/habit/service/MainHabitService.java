package io.github.shazxrin.onepercentbetter.habit.service;

import io.github.shazxrin.onepercentbetter.habit.exception.HabitNotFoundException;
import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import io.github.shazxrin.onepercentbetter.habit.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainHabitService implements HabitService {
    private final HabitRepository habitRepository;

    public MainHabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    @Override
    public void addHabit(String name, String description) {
        Habit habit = new Habit(name, description);
        habitRepository.save(habit);
    }

    @Override
    @Transactional
    public void removeHabit(Long id) {
        if (habitRepository.existsById(id)) {
            throw new HabitNotFoundException("Habit not found!");
        }

        habitRepository.deleteById(id);
    }

    @Override
    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        habitRepository.findAll()
                .forEach(habits::add);

        return habits;
    }
}
