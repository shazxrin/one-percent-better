package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class CheckInProjectAggregateYearlySummaryTest {

    @Test
    void testCreateNewSummary_shouldInitializeDistributions() {
        // Arrange & Act
        var summary = new CheckInProjectAggregateYearlySummary();

        // Assert
        assertNotNull(summary.getTypeDistribution());
        assertNotNull(summary.getHourDistribution());
        assertNotNull(summary.getProjectDistribution());
        assertNotNull(summary.getDayDistribution());
        
        assertEquals(0, summary.getTypeDistribution().size());
        assertEquals(24, summary.getHourDistribution().size());
        assertEquals(0, summary.getProjectDistribution().size());
        assertEquals(12, summary.getDayDistribution().size());
        
        // Check all hours are initialized to 0
        for (int i = 0; i < 24; i++) {
            assertEquals(0, summary.getHourDistribution().get(String.valueOf(i)));
        }
        
        // Check all months are initialized to 0
        for (int i = 1; i <= 12; i++) {
            assertEquals(0, summary.getDayDistribution().get(String.valueOf(i)));
        }
    }

    @Test
    void testCreateSummaryWithParams_shouldSetFieldsAndInitializeDistributions() {
        // Arrange
        int year = 2025;
        LocalDate startDate = LocalDate.of(2025, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(2025, Month.DECEMBER, 31);
        int noOfCheckIns = 10;
        int streak = 5;
        
        // Act
        var summary = new CheckInProjectAggregateYearlySummary(year, startDate, endDate, noOfCheckIns, streak);
        
        // Assert
        assertEquals(year, summary.getYear());
        assertEquals(startDate, summary.getStartDate());
        assertEquals(endDate, summary.getEndDate());
        assertEquals(noOfCheckIns, summary.getNoOfCheckIns());
        assertEquals(streak, summary.getStreak());
        
        assertNotNull(summary.getTypeDistribution());
        assertNotNull(summary.getHourDistribution());
        assertNotNull(summary.getProjectDistribution());
        assertNotNull(summary.getDayDistribution());
        
        assertEquals(0, summary.getTypeDistribution().size());
        assertEquals(24, summary.getHourDistribution().size());
        assertEquals(0, summary.getProjectDistribution().size());
        assertEquals(12, summary.getDayDistribution().size());
    }
}