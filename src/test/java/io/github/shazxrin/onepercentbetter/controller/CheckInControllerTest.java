package io.github.shazxrin.onepercentbetter.controller;

import io.github.shazxrin.onepercentbetter.service.checkin.CheckInService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc.perform(post("/check-ins/today"))
            .andExpect(status().isOk());

        // Then
        verify(checkInService, times(1)).checkInToday();
    }
}
