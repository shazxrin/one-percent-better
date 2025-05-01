package io.github.shazxrin.onepercentbetter.service;

import io.github.shazxrin.onepercentbetter.exception.BadRequestException;
import io.github.shazxrin.onepercentbetter.model.Project;
import io.github.shazxrin.onepercentbetter.repository.ProjectRepository;
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
        String owner = "shazxrin";
        String name = "test-project";
        when(projectRepository.existsByOwnerAndName(owner, name)).thenReturn(false);

        // When
        projectService.addProject(owner, name);

        // Then
        verify(projectRepository).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();
        assertEquals(owner, savedProject.getOwner());
        assertEquals(name, savedProject.getName());
    }

    @Test
    void testAddProject_whenProjectAlreadyExists_shouldNotSaveProject() {
        // Given
        String owner = "shazxrin";
        String name = "existing-project";
        when(projectRepository.existsByOwnerAndName(owner, name)).thenReturn(true);

        // When
        projectService.addProject(owner, name);

        // Then
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testRemoveProject_whenProjectExists_shouldDeleteProject() {
        // Given
        String owner = "shazxrin";
        String name = "project-to-delete";
        when(projectRepository.existsByOwnerAndName(owner, name)).thenReturn(true);

        // When
        projectService.removeProject(owner, name);

        // Then
        verify(projectRepository).deleteByOwnerAndName(owner, name);
    }

    @Test
    void testRemoveProject_whenProjectDoesNotExist_shouldThrowBadRequestException() {
        // Given
        String owner = "shazxrin";
        String name = "non-existent-project";
        when(projectRepository.existsByOwnerAndName(owner, name)).thenReturn(false);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            projectService.removeProject(owner, name);
        });
        assertEquals("Project not found", exception.getMessage());
        verify(projectRepository, never()).deleteByOwnerAndName(any(), any());
    }

    @Test
    void testGetAllProjects_whenProjectsExist_shouldReturnAllProjects() {
        // Given
        List<Project> projects = List.of(
            new Project("1", "shazxrin", "project1"),
            new Project("2", "shazxrin", "project2")
        );

        when(projectRepository.findAll()).thenReturn(projects);

        // When
        List<Project> result = projectService.getAllProjects();

        // Then
        assertEquals(2, result.size());
        assertEquals("project1", result.get(0).getName());
        assertEquals("project2", result.get(1).getName());
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
