package io.github.shazxrin.onepercentbetter.checkin.repository;


import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProject;
import java.time.LocalDate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInProjectRepository extends ListCrudRepository<CheckInProject, Long> {
    boolean existsByHash(String hash);

    int countByDate(LocalDate date);
}
