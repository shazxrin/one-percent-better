package io.github.shazxrin.onepercentbetter.project.controller;

import io.github.shazxrin.onepercentbetter.project.dto.AddProject;
import io.github.shazxrin.onepercentbetter.project.dto.ListItemProject;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Projects",
    description = "API for managing projects"
)
@RequestMapping("/api/projects")
@RestController
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Get all projects")
    @ApiResponse(responseCode = "200", description = "Get all projects successfully")
    @GetMapping
    public List<ListItemProject> getAllProjects() {
        ArrayList<ListItemProject> projects = new ArrayList<>();
        projectService.getAllProjects()
            .forEach(project -> projects.add(new ListItemProject(project.getId(), project.getName())));
        return projects;
    }

    @Operation(summary = "Add a new project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Add a new project successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void postAddProject(@RequestBody AddProject addProject) {
        projectService.addProject(addProject.name());
    }

    @Operation(summary = "Delete a project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delete a project successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable long id) {
        projectService.removeProject(id);
    }
}
