package io.github.shazxrin.onepercentbetter.habit.repository;

import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import org.springframework.data.repository.ListCrudRepository;

public interface HabitRepository extends ListCrudRepository<Habit, Long> {
}
