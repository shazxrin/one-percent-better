package io.github.shazxrin.onepercentbetter.checkin.summary.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectDailySummaryRepository extends ListCrudRepository<CheckInProjectDailySummary, Long> {
    Optional<CheckInProjectDailySummary> findByProjectIdAndDate(long projectId, LocalDate date);

    List<CheckInProjectDailySummary> findAllByDate(LocalDate date);
}
