package io.github.shazxrin.onepercentbetter.habit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shazxrin.onepercentbetter.habit.dto.ListItemHabit;
import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import io.github.shazxrin.onepercentbetter.habit.service.HabitService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitController.class)
public class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitService habitService;

    @Test
    void getAllHabits_shouldReturnListOfHabits() throws Exception {
        // Given
        Habit habit1 = new Habit(1L, "Read", "Read a book daily");
        Habit habit2 = new Habit(2L, "Exercise", "Go to the gym");
        List<Habit> habits = Arrays.asList(habit1, habit2);

        when(habitService.getAllHabits()).thenReturn(habits);

        List<ListItemHabit> expectedList = Arrays.asList(
            new ListItemHabit(1L, "Read"),
            new ListItemHabit(2L, "Exercise")
        );

        // When & Then
        mockMvc.perform(get("/api/habits"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name").value("Read"))
            .andExpect(jsonPath("$[1].name").value("Exercise"));
    }

    @Test
    void postAddHabit_shouldReturnCreatedStatus() throws Exception {
        // When & Then
        mockMvc.perform(
            post("/api/habits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                  {"name":  "Meditate", "description": "Meditate for 10 minutes"}
                """)
            )
            .andExpect(status().isCreated());

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);

        verify(habitService, times(1)).addHabit(nameCaptor.capture(), descriptionCaptor.capture());
        assertEquals("Meditate", nameCaptor.getValue());
        assertEquals("Meditate for 10 minutes", descriptionCaptor.getValue());
    }

    @Test
    void deleteRemoveHabit_shouldReturnOkStatus() throws Exception {
        // Given
        Long habitId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/habits/{id}", habitId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}