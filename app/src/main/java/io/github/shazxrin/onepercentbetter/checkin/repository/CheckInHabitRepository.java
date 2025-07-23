package io.github.shazxrin.onepercentbetter.checkin.repository;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInHabit;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInHabitRepository extends ListCrudRepository<CheckInHabit, Long> {
    List<CheckInHabit> findAllByDate(LocalDate date);
}
