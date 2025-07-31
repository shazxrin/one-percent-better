package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectAggregateDailySummaryService;
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

@WebMvcTest(controllers = CheckInProjectAggregateDailySummaryController.class)
public class CheckInProjectAggregateDailySummaryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;
    
    @Test
    void testGetCheckInProjectAggregateDailySummary_shouldCallServiceAndReturnSummaryAndOk() throws Exception {
        // Arrange
        LocalDate date = LocalDate.of(2025, 7, 31);
        
        CheckInProjectAggregateDailySummary summary = new CheckInProjectAggregateDailySummary(date, 10, 5);
        
        when(checkInProjectAggregateDailySummaryService.getAggregateSummary(date)).thenReturn(summary);
        
        // Act & Assert
        mockMvc.perform(get("/api/check-ins/projects/daily-summaries/aggregate/{date}", date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noOfCheckIns").value(10))
                .andExpect(jsonPath("$.streak").value(5));
        
        verify(checkInProjectAggregateDailySummaryService, times(1)).getAggregateSummary(date);
    }
    
    @Test
    void testPostCalculatesCheckInProjectAggregateDailySummary_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        LocalDate date = LocalDate.of(2025, 7, 31);
        
        // Act & Assert
        mockMvc.perform(post("/api/check-ins/projects/daily-summaries/aggregate/{date}", date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(checkInProjectAggregateDailySummaryService, times(1)).calculateAggregateSummary(date);
    }
}
