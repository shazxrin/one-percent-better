package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectWeeklySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.service.CheckInProjectWeeklySummaryService;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInProjectWeeklySummaryController.class)
public class CheckInProjectWeeklySummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService;

    @Test
    void testGetCheckInProjectWeeklySummary_shouldCallServiceAndReturnSummaryAndOk() throws Exception {
        // Arrange
        long projectId = 123L;
        int year = 2025;
        int weekNo = 31;

        Project project = new Project();
        project.setId(projectId);

        LocalDate start = LocalDate.of(2025, 7, 28).with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);
        CheckInProjectWeeklySummary summary = new CheckInProjectWeeklySummary(weekNo, year, start, end, 5, 3, project);
        summary.setTypeDistribution(new java.util.LinkedHashMap<>());
        summary.setHourDistribution(new java.util.LinkedHashMap<>());
        summary.setDayDistribution(new java.util.LinkedHashMap<>());
        summary.getTypeDistribution().put("feat", 2);
        summary.getHourDistribution().put("1", 2);
        summary.getDayDistribution().put(DayOfWeek.MONDAY.toString(), 1);

        when(checkInProjectWeeklySummaryService.getSummary(projectId, year, weekNo)).thenReturn(summary);

        // Act & Assert
        mockMvc.perform(get("/api/check-ins/projects/weekly-summaries/{projectId}/{year}/{weekNo}", projectId, year, weekNo)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startDate").value(start.toString()))
            .andExpect(jsonPath("$.endDate").value(end.toString()))
            .andExpect(jsonPath("$.noOfCheckIns").value(5))
            .andExpect(jsonPath("$.streak").value(3))
            .andExpect(jsonPath("$.typeDistribution.feat").value(2))
            .andExpect(jsonPath("$.hourDistribution['1']").value(2))
            .andExpect(jsonPath("$.dayDistribution.MONDAY").value(1));

        verify(checkInProjectWeeklySummaryService, times(1)).getSummary(projectId, year, weekNo);
    }

    @Test
    void testPostCalculatesCheckInProjectWeeklySummary_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        long projectId = 123L;
        int year = 2025;
        int weekNo = 31;

        // Act & Assert
        mockMvc.perform(post("/api/check-ins/projects/weekly-summaries/{projectId}/{year}/{weekNo}", projectId, year, weekNo)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(checkInProjectWeeklySummaryService, times(1)).calculateSummaryForWeek(projectId, year, weekNo);
    }
}
