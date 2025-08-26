package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectAggregateMonthlySummary;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectAggregateMonthlySummaryRepository
    extends ListCrudRepository<CheckInProjectAggregateMonthlySummary, Long> {
    Optional<CheckInProjectAggregateMonthlySummary> findByYearAndMonthNo(int year, int monthNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckInProjectAggregateMonthlySummary c WHERE c.year = :year AND c.monthNo = :monthNo")
    Optional<CheckInProjectAggregateMonthlySummary> findByYearAndMonthNoWithLock(int year, int monthNo);
}