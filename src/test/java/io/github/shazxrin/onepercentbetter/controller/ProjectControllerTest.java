package io.github.shazxrin.onepercentbetter.controller;

import io.github.shazxrin.onepercentbetter.model.Project;
import io.github.shazxrin.onepercentbetter.service.project.ProjectService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void testGetAllProjects_shouldReturnAllProjects() throws Exception {
        // Given
        Project project1 = new Project(null, "owner1", "repo1");
        Project project2 = new Project(null, "owner2", "repo2");
        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));

        // When & Then
        mockMvc.perform(get("/projects/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].owner").value("owner1"))
                .andExpect(jsonPath("$[0].name").value("repo1"))
                .andExpect(jsonPath("$[1].owner").value("owner2"))
                .andExpect(jsonPath("$[1].name").value("repo2"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetAllProjects_whenNoProjects_shouldReturnEmptyList() throws Exception {
        // Given
        when(projectService.getAllProjects()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/projects/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testPostAddProject_shouldCallServiceAndReturnCreated() throws Exception {
        // When & Then
        mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"owner": "testOwner","name": "testRepo"}
            """))
            .andExpect(status().isCreated());

        ArgumentCaptor<String> ownerCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        
        verify(projectService, times(1)).addProject(ownerCaptor.capture(), nameCaptor.capture());
        assertEquals("testOwner", ownerCaptor.getValue());
        assertEquals("testRepo", nameCaptor.getValue());
    }

    @Test
    void testDeleteProject_shouldCallServiceAndReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(delete("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"owner": "testOwner","name": "testRepo"}
            """))
            .andExpect(status().isOk());

        ArgumentCaptor<String> ownerCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        
        verify(projectService, times(1)).removeProject(ownerCaptor.capture(), nameCaptor.capture());
        assertEquals("testOwner", ownerCaptor.getValue());
        assertEquals("testRepo", nameCaptor.getValue());
    }
}
