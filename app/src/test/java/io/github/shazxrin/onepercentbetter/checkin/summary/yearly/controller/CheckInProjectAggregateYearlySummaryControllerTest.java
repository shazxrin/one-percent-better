package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectAggregateYearlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectAggregateYearlySummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInProjectAggregateYearlySummaryController.class)
class CheckInProjectAggregateYearlySummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectAggregateYearlySummaryService checkInProjectAggregateYearlySummaryService;

    @Test
    void testGetCheckInProjectAggregateYearlySummary_shouldCallServiceAndReturnSummaryAndOk() throws Exception {
        // Arrange
        int year = 2025;
        var startDate = LocalDate.of(2025, Month.JANUARY, 1);
        var endDate = LocalDate.of(2025, Month.DECEMBER, 31);
        
        var summary = new CheckInProjectAggregateYearlySummary(
            year, startDate, endDate, 10, 5
        );
        
        summary.setTypeDistribution(new HashMap<>() {{
            put("feat", 5);
            put("fix", 3);
            put("unknown", 2);
        }});
        
        summary.getDayDistribution().put("1", 3);
        summary.getDayDistribution().put("2", 2);
        summary.getDayDistribution().put("3", 5);
        
        when(checkInProjectAggregateYearlySummaryService.getAggregateSummary(year)).thenReturn(summary);
        
        // Act & Assert
        mockMvc.perform(get("/api/check-ins/projects/yearly-summaries/aggregate/{year}", year))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startDate").value("2025-01-01"))
            .andExpect(jsonPath("$.endDate").value("2025-12-31"))
            .andExpect(jsonPath("$.noOfCheckIns").value(10))
            .andExpect(jsonPath("$.streak").value(5))
            .andExpect(jsonPath("$.typeDistribution.feat").value(5))
            .andExpect(jsonPath("$.typeDistribution.fix").value(3))
            .andExpect(jsonPath("$.dayDistribution['1']").value(3))
            .andExpect(jsonPath("$.dayDistribution['2']").value(2))
            .andExpect(jsonPath("$.dayDistribution['3']").value(5));
            
        verify(checkInProjectAggregateYearlySummaryService).getAggregateSummary(year);
    }

    @Test
    void testPostCalculateCheckInProjectAggregateYearlySummary_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        int year = 2025;
        doNothing().when(checkInProjectAggregateYearlySummaryService).calculateAggregateSummaryForYear(year);
        
        // Act & Assert
        mockMvc.perform(post("/api/check-ins/projects/yearly-summaries/aggregate/{year}", year))
            .andExpect(status().isOk());
            
        verify(checkInProjectAggregateYearlySummaryService).calculateAggregateSummaryForYear(year);
    }
}