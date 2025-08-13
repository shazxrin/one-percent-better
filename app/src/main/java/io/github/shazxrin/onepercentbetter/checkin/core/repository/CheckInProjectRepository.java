package io.github.shazxrin.onepercentbetter.checkin.core.repository;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectRepository extends ListCrudRepository<CheckInProject, Long> {
    boolean existsByProjectIdAndHash(long projectId, String hash);

    int countByDateTimeBetween(LocalDateTime from, LocalDateTime to);

    List<CheckInProject> findByProjectIdAndDateTimeBetween(long projectId, LocalDateTime from, LocalDateTime to);
}
