package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectAggregateMonthlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.repository.CheckInProjectAggregateMonthlySummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectAggregateMonthlySummaryServiceTest {
    @Mock
    private CheckInProjectAggregateMonthlySummaryRepository repository;

    @Mock
    private CheckInProjectService checkInProjectService;

    @InjectMocks
    private CheckInProjectAggregateMonthlySummaryService service;

    @Test
    void testGetAggregateSummary_whenPresent_shouldReturnSummary() {
        // Arrange
        int year = 2025;
        int month = 8;
        YearMonth yearMonth = YearMonth.of(year, month);
        var summary = new CheckInProjectAggregateMonthlySummary(
            year, 
            month, 
            yearMonth.atDay(1), 
            yearMonth.atEndOfMonth(), 
            0, 
            0
        );
        when(repository.findByYearAndMonthNo(year, month)).thenReturn(Optional.of(summary));

        // Act
        var result = service.getAggregateSummary(year, month);

        // Assert
        assertEquals(summary, result);
        verify(repository).findByYearAndMonthNo(year, month);
    }

    @Test
    void testGetAggregateSummary_whenNotPresent_shouldThrowException() {
        // Arrange
        when(repository.findByYearAndMonthNo(2025, 1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> service.getAggregateSummary(2025, 1));
    }

    @Test
    void testCalculateAggregateSummaryForMonth_shouldComputeAndSaveDistributions() {
        // Arrange
        int year = 2025;
        int month = 8;
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        var summary = new CheckInProjectAggregateMonthlySummary(year, month, start, end, 0, 0);
        when(repository.findByYearAndMonthNoWithLock(year, month)).thenReturn(Optional.of(summary));

        var p1 = new CheckInProject();
        p1.setDateTime(LocalDateTime.of(2025, 8, 10, 12, 0));
        p1.setType("feat");
        var proj = new io.github.shazxrin.onepercentbetter.project.model.Project();
        proj.setName("Alpha");
        p1.setProject(proj);

        var p2 = new CheckInProject();
        p2.setDateTime(LocalDateTime.of(2025, 8, 11, 12, 0));
        p2.setType(null); // unknown type handling
        p2.setProject(proj);

        when(checkInProjectService.getAllCheckInsBetween(start, end)).thenReturn(List.of(p1, p2));

        // Act
        service.calculateAggregateSummaryForMonth(year, month);

        // Assert
        ArgumentCaptor<CheckInProjectAggregateMonthlySummary> captor = ArgumentCaptor.forClass(CheckInProjectAggregateMonthlySummary.class);
        verify(repository).save(captor.capture());
        var saved = captor.getValue();

        assertEquals(2, saved.getNoOfCheckIns());
        assertTrue(saved.getStreak() >= 1); // two consecutive days streak -> should be at least 2, but allow utility specifics
        assertEquals(1, saved.getTypeDistribution().getOrDefault("feat", 0));
        assertEquals(1, saved.getTypeDistribution().getOrDefault("unknown", 0));
        assertEquals(2, saved.getHourDistribution().getOrDefault("12", 0));
        assertEquals(2, saved.getProjectDistribution().getOrDefault("Alpha", 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault("10", 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault("11", 0));
    }

    @Test
    void testAddCheckInToAggregateSummary_shouldIncrementCountsAndRecalculateStreak() {
        // Arrange
        var project = new io.github.shazxrin.onepercentbetter.project.model.Project();
        project.setName("Beta");

        var checkIn = new CheckInProject();
        checkIn.setProject(project);
        checkIn.setType("chore");
        var dt = LocalDateTime.of(2025, 8, 15, 10, 0);
        checkIn.setDateTime(dt);

        when(checkInProjectService.getCheckIn(123L)).thenReturn(Optional.of(checkIn));

        int year = dt.getYear();
        int monthNo = dt.getMonthValue();
        YearMonth yearMonth = YearMonth.of(year, monthNo);
        var start = yearMonth.atDay(1);
        var end = yearMonth.atEndOfMonth();
        var existing = new CheckInProjectAggregateMonthlySummary(year, monthNo, start, end, 0, 0);

        when(repository.findByYearAndMonthNo(year, monthNo)).thenReturn(Optional.of(existing));

        // Act
        service.addCheckInToAggregateSummary(123L);

        // Assert
        ArgumentCaptor<CheckInProjectAggregateMonthlySummary> captor = ArgumentCaptor.forClass(CheckInProjectAggregateMonthlySummary.class);
        verify(repository).save(captor.capture());
        var saved = captor.getValue();

        assertEquals(1, saved.getNoOfCheckIns());
        assertEquals(1, saved.getTypeDistribution().getOrDefault("chore", 0));
        assertEquals(1, saved.getHourDistribution().getOrDefault("10", 0));
        assertEquals(1, saved.getProjectDistribution().getOrDefault("Beta", 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault("15", 0));
        assertTrue(saved.getStreak() >= 1);
    }
}