package io.github.shazxrin.onepercentbetter.project.service;

import io.github.shazxrin.onepercentbetter.project.exception.ProjectInvalidFormatException;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.repository.ProjectRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import java.util.ArrayList;
import java.util.List;

import io.github.shazxrin.onepercentbetter.utils.project.ProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void addProject(String name) {
        if (projectRepository.existsByName(name)) {
            log.info("Project {} already exists. Skipping.", name);
            return;
        }

        if (!ProjectUtil.isProjectNameValid(name)) {
            throw new ProjectInvalidFormatException("Invalid project name!");
        }

        projectRepository.save(new Project(name));
    }

    @Transactional
    public void removeProject(long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found");
        }

        projectRepository.deleteById(id);
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        projectRepository.findAll()
            .forEach(projects::add);

        return projects;
    }
}
