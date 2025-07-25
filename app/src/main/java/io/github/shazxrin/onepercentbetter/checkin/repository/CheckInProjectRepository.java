package io.github.shazxrin.onepercentbetter.checkin.repository;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProject;
import java.time.LocalDate;
import org.springframework.data.repository.ListCrudRepository;

public interface CheckInProjectRepository extends ListCrudRepository<CheckInProject, Long> {
    boolean existsByProjectIdAndHash(long projectId, String hash);

    int countByDate(LocalDate date);
}
