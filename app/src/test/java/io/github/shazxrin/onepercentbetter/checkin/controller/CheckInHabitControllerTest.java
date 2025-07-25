package io.github.shazxrin.onepercentbetter.checkin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shazxrin.onepercentbetter.checkin.dto.CheckInHabitAddRequest;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInHabitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInHabitController.class)
public class CheckInHabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CheckInHabitService checkInHabitService;

    @Test
    void testPostCheckInHabit_shouldCallServiceAndReturnCreated() throws Exception {
        // Given
        long habitId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 15);
        int amount = 30;
        String notes = "Completed 30 minutes of reading";

        CheckInHabitAddRequest checkInHabitAddRequest = new CheckInHabitAddRequest(date, amount, notes);

        // When & Then
        mockMvc.perform(post("/api/check-in/habits/{id}", habitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInHabitAddRequest)))
            .andExpect(status().isCreated());

        verify(checkInHabitService, times(1))
            .addCheckIn(eq(habitId), eq(date), eq(amount), eq(notes));
    }

    @Test
    void testPostCheckInHabit_withNullNotes_shouldCallServiceAndReturnOk() throws Exception {
        // Given
        long habitId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 15);
        int amount = 30;
        String notes = null;

        CheckInHabitAddRequest checkInHabitAddRequest = new CheckInHabitAddRequest(date, amount, notes);
        
        // When & Then
        mockMvc.perform(post("/api/check-in/habits/{id}", habitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkInHabitAddRequest)))
            .andExpect(status().isCreated());

        verify(checkInHabitService, times(1))
            .addCheckIn(eq(habitId), eq(date), eq(amount), eq(notes));
    }
}