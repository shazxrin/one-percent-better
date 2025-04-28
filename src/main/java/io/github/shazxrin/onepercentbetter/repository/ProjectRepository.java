package io.github.shazxrin.onepercentbetter.repository;

import io.github.shazxrin.onepercentbetter.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, String> {
    boolean existsByOwnerAndName(String owner, String name);

    void deleteByOwnerAndName(String owner, String name);
}
