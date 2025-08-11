package io.github.shazxrin.onepercentbetter.checkin.core.repository;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectRepository extends ListCrudRepository<CheckInProject, Long> {
    boolean existsByProjectIdAndHash(long projectId, String hash);

    int countByProjectIdAndDate(long projectId, LocalDate date);

    int countByDate(LocalDate date);

    List<CheckInProject> findByProjectIdAndDate(long projectId, LocalDate date);
}
