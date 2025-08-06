package io.github.shazxrin.onepercentbetter.habit.controller;

import io.github.shazxrin.onepercentbetter.habit.exception.HabitNotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitExceptionHandlerControllerAdviceTest.ExceptionController.class)
@Import(HabitExceptionHandlerControllerAdviceTest.ExceptionController.class)
public class HabitExceptionHandlerControllerAdviceTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHandleHabitNotFoundException_shouldReturnBadRequestStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/test/project-not-found"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.type").exists())
            .andExpect(jsonPath("$.detail").value("Habit not found."));
    }

    @RestController
    static class ExceptionController {
        @GetMapping("/test/project-not-found")
        public String badRequest() {
            throw new HabitNotFoundException();
        }
    }
}
