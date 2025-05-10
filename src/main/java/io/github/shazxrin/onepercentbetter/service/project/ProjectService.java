package io.github.shazxrin.onepercentbetter.service.project;

import io.github.shazxrin.onepercentbetter.model.Project;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectService {
    void addProject(String owner, String name);

    @Transactional
    void removeProject(String owner, String name);

    List<Project> getAllProjects();
}
