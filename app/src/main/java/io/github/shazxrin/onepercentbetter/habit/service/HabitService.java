package io.github.shazxrin.onepercentbetter.habit.service;

import io.github.shazxrin.onepercentbetter.habit.exception.HabitNotFoundException;
import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import io.github.shazxrin.onepercentbetter.habit.repository.HabitRepository;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Observed
@Service
public class HabitService {
    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public void addHabit(String name, String description) {
        Habit habit = new Habit(name, description);
        habitRepository.save(habit);
    }

    @Transactional
    public void removeHabit(Long id) {
        if (habitRepository.existsById(id)) {
            throw new HabitNotFoundException("Habit not found!");
        }

        habitRepository.deleteById(id);
    }

    public List<Habit> getAllHabits() {
        return habitRepository.findAll();
    }

    public Habit getHabitById(long id) {
        return habitRepository.findById(id)
            .orElseThrow(() -> new HabitNotFoundException("Habit not found!"));
    }
}
