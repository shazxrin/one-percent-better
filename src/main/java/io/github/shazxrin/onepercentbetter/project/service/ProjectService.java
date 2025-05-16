package io.github.shazxrin.onepercentbetter.project.service;

import io.github.shazxrin.onepercentbetter.project.model.Project;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectService {
    void addProject(String owner, String name);

    @Transactional
    void removeProject(String owner, String name);

    List<Project> getAllProjects();
}
