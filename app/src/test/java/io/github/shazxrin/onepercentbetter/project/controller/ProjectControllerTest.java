package io.github.shazxrin.onepercentbetter.project.controller;

import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
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
        Project project1 = new Project(null, "owner1/repo1");
        Project project2 = new Project(null, "owner2/repo2");
        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));

        // When & Then
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("owner1/repo1"))
                .andExpect(jsonPath("$[1].name").value("owner2/repo2"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetAllProjects_whenNoProjects_shouldReturnEmptyList() throws Exception {
        // Given
        when(projectService.getAllProjects()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testPostAddProject_shouldCallServiceAndReturnCreated() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "testOwner/testRepo"}
            """))
            .andExpect(status().isCreated());

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        
        verify(projectService, times(1)).addProject(nameCaptor.capture());
        assertEquals("testRepo", nameCaptor.getValue());
    }

    @Test
    void testDeleteProject_shouldCallServiceAndReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(
                delete("/api/projects/1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        
        verify(projectService, times(1)).removeProject(idCaptor.capture());
        assertEquals(1L, idCaptor.getValue());
    }
}
