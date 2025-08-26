package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectWeeklySummary;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectWeeklySummaryRepository extends ListCrudRepository<CheckInProjectWeeklySummary, Long> {
    Optional<CheckInProjectWeeklySummary> findByProjectIdAndYearAndWeekNo(long projectId, int year, int weekNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckInProjectWeeklySummary c WHERE c.project.id = :projectId AND c.year = :year AND c.weekNo = :weekNo")
    Optional<CheckInProjectWeeklySummary> findByProjectIdAndYearAndWeekNoWithLock(long projectId, int year, int weekNo);
}
