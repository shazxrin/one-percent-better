package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectYearlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectYearlySummaryService;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInProjectYearlySummaryController.class)
public class CheckInProjectYearlySummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectYearlySummaryService checkInProjectYearlySummaryService;

    @Test
    void testGetCheckInProjectYearlySummary_shouldCallServiceAndReturnSummaryAndOk() throws Exception {
        long projectId = 123L;
        int year = 2025;

        Project project = new Project();
        project.setId(projectId);

        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        CheckInProjectYearlySummary summary = new CheckInProjectYearlySummary(year, start, end, 5, 3, project);
        summary.setTypeDistribution(new LinkedHashMap<>());
        summary.setHourDistribution(new LinkedHashMap<>());
        summary.setDayDistribution(new LinkedHashMap<>());
        summary.getTypeDistribution().put("feat", 2);
        summary.getHourDistribution().put("1", 2);
        summary.getDayDistribution().put("1", 1);

        when(checkInProjectYearlySummaryService.getSummary(projectId, year)).thenReturn(summary);

        mockMvc.perform(get("/api/check-ins/projects/yearly-summaries/{projectId}/{year}", projectId, year)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startDate").value(start.toString()))
            .andExpect(jsonPath("$.endDate").value(end.toString()))
            .andExpect(jsonPath("$.noOfCheckIns").value(5))
            .andExpect(jsonPath("$.streak").value(3))
            .andExpect(jsonPath("$.typeDistribution.feat").value(2))
            .andExpect(jsonPath("$.hourDistribution['1']").value(2))
            .andExpect(jsonPath("$.dayDistribution['1']").value(1));

        verify(checkInProjectYearlySummaryService, times(1)).getSummary(projectId, year);
    }

    @Test
    void testPostCalculatesCheckInProjectYearlySummary_shouldCallServiceAndReturnOk() throws Exception {
        long projectId = 123L;
        int year = 2025;

        mockMvc.perform(post("/api/check-ins/projects/yearly-summaries/{projectId}/{year}", projectId, year)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(checkInProjectYearlySummaryService, times(1)).calculateSummaryForYear(projectId, year);
    }
}