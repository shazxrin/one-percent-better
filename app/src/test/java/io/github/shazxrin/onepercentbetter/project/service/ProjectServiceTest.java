package io.github.shazxrin.onepercentbetter.project.service;

import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.repository.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @Test
    void testAddProject_whenProjectDoesNotExist_shouldSaveProject() {
        // Given
        String name = "shazxrin/test-project";
        when(projectRepository.existsByName(name)).thenReturn(false);

        // When
        projectService.addProject(name);

        // Then
        verify(projectRepository).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();
        assertEquals(name, savedProject.getName());
    }

    @Test
    void testAddProject_whenProjectAlreadyExists_shouldNotSaveProject() {
        // Given
        String name = "shazxrin/existing-project";
        when(projectRepository.existsByName(name)).thenReturn(true);

        // When
        projectService.addProject(name);

        // Then
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testRemoveProject_whenProjectExists_shouldDeleteProject() {
        // Given
        long id = 1L;
        when(projectRepository.existsById(id)).thenReturn(true);

        // When
        projectService.removeProject(id);

        // Then
        verify(projectRepository).deleteById(id);
    }

    @Test
    void testRemoveProject_whenProjectDoesNotExist_shouldThrowBadRequestException() {
        // Given
        long id = 1L;
        when(projectRepository.existsById(id)).thenReturn(false);

        // When & Then
        ProjectNotFoundException exception = assertThrows(
            ProjectNotFoundException.class, () -> {
            projectService.removeProject(id);
        });
        assertEquals("Project not found.", exception.getMessage());
        verify(projectRepository, never()).deleteById(any());
    }

    @Test
    void testGetAllProjects_whenProjectsExist_shouldReturnAllProjects() {
        // Given
        List<Project> projects = List.of(
            new Project(1L, "shazxrin/project1"),
            new Project(2L, "shazxrin/project2")
        );

        when(projectRepository.findAll()).thenReturn(projects);

        // When
        List<Project> result = projectService.getAllProjects();

        // Then
        assertEquals(2, result.size());
        assertEquals("shazxrin/project1", result.get(0).getName());
        assertEquals("shazxrin/project2", result.get(1).getName());
    }

    @Test
    void testGetAllProjects_whenNoProjectsExist_shouldReturnEmptyList() {
        // Given
        when(projectRepository.findAll()).thenReturn(List.of());

        // When
        List<Project> result = projectService.getAllProjects();

        // Then
        assertEquals(0, result.size());
    }
}
