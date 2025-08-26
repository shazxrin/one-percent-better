package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectAggregateWeeklySummary;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectAggregateWeeklySummaryRepository
    extends ListCrudRepository<CheckInProjectAggregateWeeklySummary, Long> {
    Optional<CheckInProjectAggregateWeeklySummary> findByYearAndWeekNo(int year, int weekNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckInProjectAggregateWeeklySummary c WHERE c.year = :year AND c.weekNo = :weekNo")
    Optional<CheckInProjectAggregateWeeklySummary> findByYearAndWeekNoWithLock(int year, int weekNo);
}
