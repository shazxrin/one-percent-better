package io.github.shazxrin.onepercentbetter.project.repository;

import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {
    boolean existsByName(String name);
}
