package io.github.shazxrin.onepercentbetter.checkin.summary.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectDailySummaryService;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInProjectDailySummaryController.class)
public class CheckInProjectDailySummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    @Test
    void testGetCheckInProjectDailySummary_shouldCallServiceAndReturnSummaryAndOk() throws Exception {
        // Arrange
        long projectId = 123L;
        LocalDate date = LocalDate.of(2025, 7, 31);
        
        Project project = new Project();
        project.setId(projectId);
        
        CheckInProjectDailySummary summary = new CheckInProjectDailySummary(date, 5, 3, project);
        summary.getTypeDistribution().put("feat", 2);
        
        when(checkInProjectDailySummaryService.getSummary(projectId, date)).thenReturn(summary);
        
        // Act & Assert
        mockMvc.perform(get("/api/check-ins/projects/daily-summaries/{projectId}/{date}", projectId, date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noOfCheckIns").value(5))
                .andExpect(jsonPath("$.streak").value(3))
                .andExpect(jsonPath("$.typeDistribution.feat").value(2));

        verify(checkInProjectDailySummaryService, times(1)).getSummary(projectId, date);
    }
    
    @Test
    void testPostCalculatesCheckInProjectDailySummary_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        long projectId = 123L;
        LocalDate date = LocalDate.of(2025, 7, 31);
        
        // Act & Assert
        mockMvc.perform(post("/api/check-ins/projects/daily-summaries/{projectId}/{date}", projectId, date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(checkInProjectDailySummaryService, times(1)).calculateSummaryForDate(projectId, date, true);
    }
}