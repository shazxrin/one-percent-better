package io.github.shazxrin.onepercentbetter.project.service;

import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.repository.ProjectRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MainProjectService implements ProjectService {
    private final ProjectRepository projectRepository;

    public MainProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void addProject(String owner, String name) {
        if (projectRepository.existsByOwnerAndName(owner, name)) {
            return;
        }

        projectRepository.save(new Project(owner, name));
    }

    @Transactional
    @Override
    public void removeProject(String owner, String name) {
        if (!projectRepository.existsByOwnerAndName(owner, name)) {
            throw new ProjectNotFoundException("Project not found");
        }

        projectRepository.deleteByOwnerAndName(owner, name);
    }

    @Override
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        projectRepository.findAll()
            .forEach(projects::add);

        return projects;
    }
}
