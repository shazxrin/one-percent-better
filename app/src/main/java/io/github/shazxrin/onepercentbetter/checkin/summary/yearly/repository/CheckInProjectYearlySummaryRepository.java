package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectYearlySummary;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface CheckInProjectYearlySummaryRepository extends ListCrudRepository<CheckInProjectYearlySummary, Long> {
    Optional<CheckInProjectYearlySummary> findByProjectIdAndYear(long projectId, int year);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckInProjectYearlySummary c WHERE c.project.id = :projectId AND c.year = :year")
    Optional<CheckInProjectYearlySummary> findByProjectIdAndYearWithLock(long projectId, int year);
}