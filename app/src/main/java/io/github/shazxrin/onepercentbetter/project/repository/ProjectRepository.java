package io.github.shazxrin.onepercentbetter.project.repository;

import io.github.shazxrin.onepercentbetter.project.model.Project;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface ProjectRepository extends ListCrudRepository<Project, Long> {
    boolean existsByName(String name);

    Optional<Project> findByName(String name);
}
