package io.github.shazxrin.onepercentbetter.checkin.repository;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectAggregateDailySummary;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CheckInProjectAggregateDailySummaryRepository
    extends ListCrudRepository<CheckInProjectAggregateDailySummary, Long> {
    Optional<CheckInProjectAggregateDailySummary> findByDate(LocalDate date);
}
