package io.github.shazxrin.onepercentbetter.checkin.core.repository;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectRepository extends ListCrudRepository<CheckInProject, Long> {
    boolean existsByProjectIdAndHash(long projectId, String hash);

    List<CheckInProject> findByProjectIdAndDateTimeBetween(long projectId, LocalDateTime from, LocalDateTime to);

    List<CheckInProject> findByDateTimeBetween(LocalDateTime from, LocalDateTime to);
}
