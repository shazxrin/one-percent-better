package io.github.shazxrin.onepercentbetter.project.repository;

import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.springframework.data.repository.ListCrudRepository;

public interface ProjectRepository extends ListCrudRepository<Project, Long> {
    boolean existsByName(String name);
}
