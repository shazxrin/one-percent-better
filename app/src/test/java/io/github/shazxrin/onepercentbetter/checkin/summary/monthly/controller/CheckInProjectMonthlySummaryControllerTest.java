package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectMonthlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service.CheckInProjectMonthlySummaryService;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInProjectMonthlySummaryController.class)
public class CheckInProjectMonthlySummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectMonthlySummaryService checkInProjectMonthlySummaryService;

    @Test
    void testGetCheckInProjectMonthlySummary_shouldCallServiceAndReturnSummaryAndOk() throws Exception {
        long projectId = 123L;
        int year = 2025;
        int monthNo = 7;

        Project project = new Project();
        project.setId(projectId);

        LocalDate start = LocalDate.of(2025, 7, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        CheckInProjectMonthlySummary summary = new CheckInProjectMonthlySummary(year, monthNo, start, end, 5, 3, project);
        summary.setTypeDistribution(new LinkedHashMap<>());
        summary.setHourDistribution(new LinkedHashMap<>());
        summary.setDayDistribution(new LinkedHashMap<>());
        summary.getTypeDistribution().put("feat", 2);
        summary.getHourDistribution().put("1", 2);
        summary.getDayDistribution().put("1", 1);

        when(checkInProjectMonthlySummaryService.getSummary(projectId, year, monthNo)).thenReturn(summary);

        mockMvc.perform(get("/api/check-ins/projects/monthly-summaries/{projectId}/{year}/{monthNo}", projectId, year, monthNo)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startDate").value(start.toString()))
            .andExpect(jsonPath("$.endDate").value(end.toString()))
            .andExpect(jsonPath("$.noOfCheckIns").value(5))
            .andExpect(jsonPath("$.streak").value(3))
            .andExpect(jsonPath("$.typeDistribution.feat").value(2))
            .andExpect(jsonPath("$.hourDistribution['1']").value(2))
            .andExpect(jsonPath("$.dateDistribution['1']").value(1));

        verify(checkInProjectMonthlySummaryService, times(1)).getSummary(projectId, year, monthNo);
    }

    @Test
    void testPostCalculatesCheckInProjectMonthlySummary_shouldCallServiceAndReturnOk() throws Exception {
        long projectId = 123L;
        int year = 2025;
        int monthNo = 7;

        mockMvc.perform(post("/api/check-ins/projects/monthly-summaries/{projectId}/{year}/{monthNo}", projectId, year, monthNo)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(checkInProjectMonthlySummaryService, times(1)).calculateSummaryForMonth(projectId, year, monthNo);
    }
}
