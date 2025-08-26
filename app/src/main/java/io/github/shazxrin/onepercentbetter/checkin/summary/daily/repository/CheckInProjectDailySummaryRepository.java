package io.github.shazxrin.onepercentbetter.checkin.summary.daily.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.daily.model.CheckInProjectDailySummary;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectDailySummaryRepository extends ListCrudRepository<CheckInProjectDailySummary, Long> {
    Optional<CheckInProjectDailySummary> findByProjectIdAndDate(long projectId, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckInProjectDailySummary c WHERE c.project.id = :projectId AND c.date = :date")
    Optional<CheckInProjectDailySummary> findByProjectIdAndDateWithLock(long projectId, LocalDate date);
}
