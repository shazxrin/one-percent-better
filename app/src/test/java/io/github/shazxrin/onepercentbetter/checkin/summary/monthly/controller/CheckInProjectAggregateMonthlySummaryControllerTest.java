package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectAggregateMonthlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service.CheckInProjectAggregateMonthlySummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CheckInProjectAggregateMonthlySummaryController.class)
public class CheckInProjectAggregateMonthlySummaryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInProjectAggregateMonthlySummaryService checkInProjectAggregateMonthlySummaryService;

    @Test
    void testGetCheckInProjectAggregateMonthlySummary_shouldCallServiceAndReturnSummaryAndOk() throws Exception {
        // Arrange
        int year = 2025;
        int monthNo = 8;
        YearMonth yearMonth = YearMonth.of(year, monthNo);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        CheckInProjectAggregateMonthlySummary summary = new CheckInProjectAggregateMonthlySummary(year, monthNo, start, end, 10, 3);
        summary.getTypeDistribution().put("feat", 2);
        summary.getHourDistribution().put("12", 2);
        summary.getProjectDistribution().put("projA", 2);
        summary.getDayDistribution().put("15", 1);

        when(checkInProjectAggregateMonthlySummaryService.getAggregateSummary(year, monthNo)).thenReturn(summary);

        // Act & Assert
        mockMvc.perform(get("/api/check-ins/projects/monthly-summaries/aggregate/{year}/{monthNo}", year, monthNo)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startDate").value(start.toString()))
            .andExpect(jsonPath("$.endDate").value(end.toString()))
            .andExpect(jsonPath("$.noOfCheckIns").value(10))
            .andExpect(jsonPath("$.streak").value(3))
            .andExpect(jsonPath("$.typeDistribution.feat").value(2))
            .andExpect(jsonPath("$.hourDistribution['12']").value(2))
            .andExpect(jsonPath("$.projectDistribution.projA").value(2))
            .andExpect(jsonPath("$.dateDistribution['15']").value(1));

        verify(checkInProjectAggregateMonthlySummaryService, times(1)).getAggregateSummary(year, monthNo);
    }

    @Test
    void testPostCalculateCheckInProjectAggregateMonthlySummary_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        int year = 2025;
        int monthNo = 8;

        // Act & Assert
        mockMvc.perform(post("/api/check-ins/projects/monthly-summaries/aggregate/{year}/{monthNo}", year, monthNo)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(checkInProjectAggregateMonthlySummaryService, times(1)).calculateAggregateSummaryForMonth(year, monthNo);
    }
}