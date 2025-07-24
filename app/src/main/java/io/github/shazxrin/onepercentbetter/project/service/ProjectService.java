package io.github.shazxrin.onepercentbetter.project.service;

import io.github.shazxrin.onepercentbetter.project.exception.ProjectInvalidFormatException;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.repository.ProjectRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.annotation.SpanTag;
import java.util.List;

import io.github.shazxrin.onepercentbetter.utils.project.ProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Observed
@Service
public class ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void addProject(@SpanTag String name) {
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
    public void removeProject(@SpanTag long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found");
        }

        projectRepository.deleteById(id);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(@SpanTag long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }
}
