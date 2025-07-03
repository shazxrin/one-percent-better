package io.github.shazxrin.onepercentbetter.habit.repository;

import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends CrudRepository<Habit, Long> { }
