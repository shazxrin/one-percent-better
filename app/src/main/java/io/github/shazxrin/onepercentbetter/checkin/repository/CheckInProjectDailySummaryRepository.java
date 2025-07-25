package io.github.shazxrin.onepercentbetter.checkin.repository;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectDailySummary;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInProjectDailySummaryRepository extends ListCrudRepository<CheckInProjectDailySummary, Long> {
    Optional<CheckInProjectDailySummary> findByProjectIdAndDate(long projectId, LocalDate date);

    List<CheckInProjectDailySummary> findAllByDate(LocalDate date);
}
