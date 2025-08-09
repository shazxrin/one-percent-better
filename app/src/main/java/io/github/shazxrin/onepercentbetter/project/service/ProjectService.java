package io.github.shazxrin.onepercentbetter.project.service;

import io.github.shazxrin.onepercentbetter.project.event.ProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectInvalidFormatException;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.repository.ProjectRepository;
import io.github.shazxrin.onepercentbetter.utils.project.ProjectUtil;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.annotation.SpanTag;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Observed
@Service
public class ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ProjectService(ProjectRepository projectRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.projectRepository = projectRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void addProject(@SpanTag String name) {
        if (projectRepository.existsByName(name)) {
            log.info("Project {} already exists. Skipping.", name);
            return;
        }

        if (!ProjectUtil.isProjectNameValid(name)) {
            throw new ProjectInvalidFormatException("Invalid project name!");
        }

        var project = projectRepository.save(new Project(name));

        applicationEventPublisher.publishEvent(
            new ProjectAddedEvent(this, project.getId())
        );
    }

    @Transactional
    public void removeProject(@SpanTag long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException();
        }

        projectRepository.deleteById(id);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(@SpanTag long id) {
        return projectRepository.findById(id);
    }
}
