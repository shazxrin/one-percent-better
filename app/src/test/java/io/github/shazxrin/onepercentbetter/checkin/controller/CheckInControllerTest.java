package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@WebMvcTest(CheckInProjectController.class)
public class CheckInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectService checkInService;

    @Test
    void testCheckInToday_shouldCallServiceAndReturnOk() throws Exception {
        // When
        mockMvc.perform(post("/api/check-ins/today"))
            .andExpect(status().isOk());

        // Then
        verify(checkInService, times(1)).checkInAll(LocalDate.now());
    }
}
