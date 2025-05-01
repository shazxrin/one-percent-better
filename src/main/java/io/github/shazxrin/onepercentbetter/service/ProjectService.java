package io.github.shazxrin.onepercentbetter.service;

import io.github.shazxrin.onepercentbetter.exception.BadRequestException;
import io.github.shazxrin.onepercentbetter.model.Project;
import io.github.shazxrin.onepercentbetter.repository.ProjectRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public void addProject(String owner, String name) {
        if (projectRepository.existsByOwnerAndName(owner, name)) {
            return;
        }

        projectRepository.save(new Project(null, owner, name));
    }

    @Transactional
    public void removeProject(String owner, String name) {
        if (!projectRepository.existsByOwnerAndName(owner, name)) {
            throw new BadRequestException("Project not found");
        }

        projectRepository.deleteByOwnerAndName(owner, name);
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        projectRepository.findAll()
            .forEach(projects::add);

        return projects;
    }
}
