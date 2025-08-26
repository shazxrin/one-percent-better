package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectMonthlySummary;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface CheckInProjectMonthlySummaryRepository extends ListCrudRepository<CheckInProjectMonthlySummary, Long> {
    Optional<CheckInProjectMonthlySummary> findByProjectIdAndYearAndMonthNo(long projectId, int year, int monthNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckInProjectMonthlySummary c WHERE c.project.id = :projectId AND c.year = :year AND c.monthNo = :monthNo")
    Optional<CheckInProjectMonthlySummary> findByProjectIdAndYearAndMonthNoWithLock(long projectId, int year, int monthNo);
}
