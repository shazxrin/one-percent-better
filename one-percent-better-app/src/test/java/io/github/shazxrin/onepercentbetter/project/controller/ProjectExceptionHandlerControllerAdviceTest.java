package io.github.shazxrin.onepercentbetter.project.controller;

import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectExceptionHandlerControllerAdviceTest.ExceptionController.class)
@Import(ProjectExceptionHandlerControllerAdviceTest.ExceptionController.class)
public class ProjectExceptionHandlerControllerAdviceTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHandleProjectNotFoundException_shouldReturnBadRequestStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/test/project-not-found"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.type").exists())
            .andExpect(jsonPath("$.detail").value("Bad request."));
    }

    @Test
    void testNormalEndpoint_shouldReturnOkStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/test/ok"))
            .andExpect(status().isOk())
            .andExpect(content().string("OK"));
    }

    @RestController
    static class ExceptionController {
        @GetMapping("/test/project-not-found")
        public String badRequest() {
            throw new ProjectNotFoundException("Bad request.");
        }

        @GetMapping("/test/ok")
        public String ok() {
            return "OK";
        }
    }
}
