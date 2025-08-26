package io.github.shazxrin.onepercentbetter.checkin.summary.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateWeeklySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectAggregateWeeklySummaryService;
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

@WebMvcTest(controllers = CheckInProjectAggregateWeeklySummaryController.class)
public class CheckInProjectAggregateWeeklySummaryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectAggregateWeeklySummaryService checkInProjectAggregateWeeklySummaryService;

    @Test
    void testGetCheckInProjectAggregateWeeklySummary_shouldCallServiceAndReturnSummaryAndOk() throws Exception {
        // Arrange
        int year = 2025;
        int weekNo = 31;
        LocalDate start = LocalDate.of(2025, 7, 28); // Monday
        LocalDate end = start.plusDays(6); // Sunday

        CheckInProjectAggregateWeeklySummary summary = new CheckInProjectAggregateWeeklySummary(year, weekNo, start, end, 10, 3);
        summary.getTypeDistribution().put("feat", 2);
        summary.getHourDistribution().put("12", 2);
        summary.getProjectDistribution().put("projA", 2);
        summary.getDayDistribution().put(DayOfWeek.MONDAY.toString(), 1);

        when(checkInProjectAggregateWeeklySummaryService.getAggregateSummary(year, weekNo)).thenReturn(summary);

        // Act & Assert
        mockMvc.perform(get("/api/check-ins/projects/weekly-summaries/aggregate/{year}/{weekNo}", year, weekNo)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startDate").value(start.toString()))
            .andExpect(jsonPath("$.endDate").value(end.toString()))
            .andExpect(jsonPath("$.noOfCheckIns").value(10))
            .andExpect(jsonPath("$.streak").value(3))
            .andExpect(jsonPath("$.typeDistribution.feat").value(2))
            .andExpect(jsonPath("$.hourDistribution['12']").value(2))
            .andExpect(jsonPath("$.projectDistribution.projA").value(2))
            .andExpect(jsonPath("$.dayDistribution.MONDAY").value(1));

        verify(checkInProjectAggregateWeeklySummaryService, times(1)).getAggregateSummary(year, weekNo);
    }

    @Test
    void testPostCalculatesCheckInProjectAggregateWeeklySummary_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        int year = 2025;
        int weekNo = 31;

        // Act & Assert
        mockMvc.perform(post("/api/check-ins/projects/weekly-summaries/aggregate/{year}/{weekNo}", year, weekNo)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(checkInProjectAggregateWeeklySummaryService, times(1)).calculateAggregateSummaryForWeek(year, weekNo);
    }
}