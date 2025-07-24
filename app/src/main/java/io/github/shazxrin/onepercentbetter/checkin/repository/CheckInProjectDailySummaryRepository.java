package io.github.shazxrin.onepercentbetter.checkin.repository;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectDailySummary;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CheckInProjectDailySummaryRepository extends ListCrudRepository<CheckInProjectDailySummary, Long> {
    Optional<CheckInProjectDailySummary> findByProjectIdAndDate(long projectId, LocalDate date);
}
