package io.github.shazxrin.onepercentbetter.checkin.summary.daily.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.daily.model.CheckInProjectAggregateDailySummary;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectAggregateDailySummaryRepository
    extends ListCrudRepository<CheckInProjectAggregateDailySummary, Long> {
    Optional<CheckInProjectAggregateDailySummary> findByDate(LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM CheckInProjectAggregateDailySummary s WHERE s.date = :date")
    Optional<CheckInProjectAggregateDailySummary> findByDateWithLock(LocalDate date);
}
