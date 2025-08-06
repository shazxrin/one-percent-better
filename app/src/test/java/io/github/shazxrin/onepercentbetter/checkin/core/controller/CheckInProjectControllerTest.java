package io.github.shazxrin.onepercentbetter.checkin.core.controller;

import io.github.shazxrin.onepercentbetter.checkin.core.controller.CheckInProjectController;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProjectSource;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInProjectController.class)
public class CheckInProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectService checkInProjectService;

    @Test
    void testPostCheckInProjectAll_shouldCallServiceAndReturnOk() throws Exception {
        LocalDate date = LocalDate.now();
        mockMvc.perform(post("/api/check-ins/projects/all")
                .param("date", date.toString()))
                .andExpect(status().isOk());
        verify(checkInProjectService, times(1)).checkInAll(date, CheckInProjectSource.MANUAL);
    }

    @Test
    void testPostCheckInProject_shouldCallServiceAndReturnOk() throws Exception {
        long projectId = 123L;
        LocalDate date = LocalDate.now();
        mockMvc.perform(post("/api/check-ins/projects/{projectId}", projectId)
                .param("date", date.toString()))
                .andExpect(status().isOk());
        verify(checkInProjectService, times(1)).checkIn(projectId, date, CheckInProjectSource.MANUAL);
    }

    @Test
    void testPostCheckInProjectAllInterval_shouldCallServiceAndReturnOk() throws Exception {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(5);
        mockMvc.perform(post("/api/check-ins/projects/interval/all")
                .param("fromDate", fromDate.toString())
                .param("toDate", toDate.toString()))
                .andExpect(status().isOk());
        verify(checkInProjectService, times(1)).checkInAllInterval(fromDate, toDate, CheckInProjectSource.MANUAL);
    }

    @Test
    void testPostCheckInProjectInterval_shouldCallServiceAndReturnOk() throws Exception {
        long projectId = 123L;
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(5);
        mockMvc.perform(post("/api/check-ins/projects/interval/{projectId}", projectId)
                .param("fromDate", fromDate.toString())
                .param("toDate", toDate.toString()))
                .andExpect(status().isOk());
        verify(checkInProjectService, times(1)).checkInInterval(projectId, fromDate, toDate, CheckInProjectSource.MANUAL);
    }
}