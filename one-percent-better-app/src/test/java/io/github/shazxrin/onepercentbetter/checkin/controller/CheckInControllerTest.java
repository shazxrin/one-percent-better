package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInController.class)
public class CheckInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInService checkInService;

    @Test
    void testCheckInToday_shouldCallServiceAndReturnOk() throws Exception {
        // When
        mockMvc.perform(post("/api/check-ins/today"))
            .andExpect(status().isOk());

        // Then
        verify(checkInService, times(1)).checkInToday();
    }
    
    @Test
    void testGetCheckInToday_shouldReturnTodaysCheckInInfo() throws Exception {
        // Given
        CheckIn mockCheckIn = new CheckIn();
        mockCheckIn.setCount(5);
        mockCheckIn.setStreak(3);
        when(checkInService.getTodaysCheckIn()).thenReturn(mockCheckIn);
    
        // When & Then
        mockMvc.perform(get("/api/check-ins/today"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(5))
            .andExpect(jsonPath("$.streak").value(3));
    
        verify(checkInService, times(1)).getTodaysCheckIn();
    }
}
