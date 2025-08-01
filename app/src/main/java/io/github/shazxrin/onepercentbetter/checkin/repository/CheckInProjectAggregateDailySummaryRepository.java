package io.github.shazxrin.onepercentbetter.checkin.repository;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectAggregateDailySummary;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectAggregateDailySummaryRepository
    extends ListCrudRepository<CheckInProjectAggregateDailySummary, Long> {
    Optional<CheckInProjectAggregateDailySummary> findByDate(LocalDate date);
}
