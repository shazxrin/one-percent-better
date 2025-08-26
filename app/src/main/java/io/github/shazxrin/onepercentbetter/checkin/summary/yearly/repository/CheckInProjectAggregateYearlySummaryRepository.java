package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectAggregateYearlySummary;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectAggregateYearlySummaryRepository
    extends ListCrudRepository<CheckInProjectAggregateYearlySummary, Long> {
    
    Optional<CheckInProjectAggregateYearlySummary> findByYear(int year);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckInProjectAggregateYearlySummary c WHERE c.year = :year")
    Optional<CheckInProjectAggregateYearlySummary> findByYearWithLock(int year);
}