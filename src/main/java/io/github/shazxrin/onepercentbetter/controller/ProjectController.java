package io.github.shazxrin.onepercentbetter.controller;

import io.github.shazxrin.onepercentbetter.dto.AddProjectDto;
import io.github.shazxrin.onepercentbetter.dto.DeleteProjectDto;
import io.github.shazxrin.onepercentbetter.dto.ListItemProjectDto;
import io.github.shazxrin.onepercentbetter.service.ProjectService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/projects")
@RestController
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/all")
    public List<ListItemProjectDto> getAllProjects() {
        ArrayList<ListItemProjectDto> projects = new ArrayList<>();
        projectService.getAllProjects()
            .forEach(project -> projects.add(new ListItemProjectDto(project.getOwner(), project.getName())));
        return projects;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void postAddProject(@RequestBody AddProjectDto addProjectDto) {
        projectService.addProject(addProjectDto.owner(), addProjectDto.name());
    }

    @DeleteMapping
    public void deleteProject(@RequestBody DeleteProjectDto deleteProjectDto) {
        projectService.removeProject(deleteProjectDto.owner(), deleteProjectDto.name());
    }
}
