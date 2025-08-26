package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectAggregateYearlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.repository.CheckInProjectAggregateYearlySummaryRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectAggregateYearlySummaryServiceTest {
    @Mock
    private CheckInProjectAggregateYearlySummaryRepository checkInProjectAggregateYearlySummaryRepository;

    @Mock
    private CheckInProjectService checkInProjectService;

    @InjectMocks
    private CheckInProjectAggregateYearlySummaryService checkInProjectAggregateYearlySummaryService;

    @Test
    void testGetAggregateSummary_whenPresent_shouldReturnSummary() {
        // Arrange
        int year = 2025;
        var summary = new CheckInProjectAggregateYearlySummary(
            year,
            LocalDate.of(2025, Month.JANUARY, 1),
            LocalDate.of(2025, Month.DECEMBER, 31),
            0,
            0
        );
        when(checkInProjectAggregateYearlySummaryRepository.findByYear(year)).thenReturn(Optional.of(summary));

        // Act
        var result = checkInProjectAggregateYearlySummaryService.getAggregateSummary(year);

        // Assert
        assertEquals(summary, result);
        verify(checkInProjectAggregateYearlySummaryRepository).findByYear(year);
    }

    @Test
    void testGetAggregateSummary_whenNotPresent_shouldThrowException() {
        // Arrange
        when(checkInProjectAggregateYearlySummaryRepository.findByYear(2025)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> checkInProjectAggregateYearlySummaryService.getAggregateSummary(2025));
    }

    @Test
    void testCalculateAggregateSummaryForYear_shouldComputeAndSaveDistributions() {
        // Arrange
        int year = 2025;
        LocalDate start = LocalDate.of(2025, Month.JANUARY, 1);
        LocalDate end = LocalDate.of(2025, Month.DECEMBER, 31);
        var summary = new CheckInProjectAggregateYearlySummary(year, start, end, 0, 0);
        when(checkInProjectAggregateYearlySummaryRepository.findByYearWithLock(year)).thenReturn(Optional.of(summary));

        var p1 = new CheckInProject();
        var p1dt = LocalDateTime.of(2025, 1, 15, 12, 0);
        p1.setDateTime(p1dt);
        p1.setType("feat");
        var proj = new Project();
        proj.setName("Alpha");
        p1.setProject(proj);

        var p2 = new CheckInProject();
        var p2dt = LocalDateTime.of(2025, 1, 20, 14, 0);
        p2.setDateTime(p2dt);
        p2.setType(null); // unknown type handling
        p2.setProject(proj);

        when(checkInProjectService.getAllCheckInsBetween(start, end)).thenReturn(List.of(p1, p2));

        // Act
        checkInProjectAggregateYearlySummaryService.calculateAggregateSummaryForYear(year);

        // Assert
        ArgumentCaptor<CheckInProjectAggregateYearlySummary> captor = ArgumentCaptor.forClass(CheckInProjectAggregateYearlySummary.class);
        verify(checkInProjectAggregateYearlySummaryRepository).save(captor.capture());
        var saved = captor.getValue();

        assertEquals(2, saved.getNoOfCheckIns());
        assertTrue(saved.getStreak() >= 1);
        assertEquals(1, saved.getTypeDistribution().getOrDefault("feat", 0));
        assertEquals(1, saved.getTypeDistribution().getOrDefault("unknown", 0));
        assertEquals(1, saved.getHourDistribution().getOrDefault("12", 0));
        assertEquals(1, saved.getHourDistribution().getOrDefault("14", 0));
        assertEquals(2, saved.getProjectDistribution().getOrDefault("Alpha", 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault(String.valueOf(p1dt.getDayOfYear()), 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault(String.valueOf(p2dt.getDayOfYear()), 0));
    }

    @Test
    void testAddCheckInToAggregateSummary_shouldIncrementCountsAndRecalculateStreak() {
        // Arrange
        var project = new io.github.shazxrin.onepercentbetter.project.model.Project();
        project.setName("Beta");

        var checkIn = new CheckInProject();
        checkIn.setProject(project);
        checkIn.setType("chore");
        var dt = LocalDateTime.of(2025, 3, 15, 10, 0);
        checkIn.setDateTime(dt);

        when(checkInProjectService.getCheckIn(123L)).thenReturn(Optional.of(checkIn));

        int year = dt.getYear();
        var start = LocalDate.of(year, 1, 1);
        var end = LocalDate.of(year, 12, 31);
        var existing = new CheckInProjectAggregateYearlySummary(year, start, end, 0, 0);

        when(checkInProjectAggregateYearlySummaryRepository.findByYearWithLock(year)).thenReturn(Optional.of(existing));
        when(checkInProjectService.getAllCheckInsBetween(start, end)).thenReturn(List.of(checkIn));

        // Act
        checkInProjectAggregateYearlySummaryService.addCheckInToAggregateSummary(123L);

        // Assert
        ArgumentCaptor<CheckInProjectAggregateYearlySummary> captor = ArgumentCaptor.forClass(CheckInProjectAggregateYearlySummary.class);
        verify(checkInProjectAggregateYearlySummaryRepository).save(captor.capture());
        var saved = captor.getValue();

        assertEquals(1, saved.getNoOfCheckIns());
        assertEquals(1, saved.getTypeDistribution().getOrDefault("chore", 0));
        assertEquals(1, saved.getHourDistribution().getOrDefault("10", 0));
        assertEquals(1, saved.getProjectDistribution().getOrDefault("Beta", 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault(String.valueOf(dt.getDayOfYear()), 0));
        assertTrue(saved.getStreak() >= 1);
    }

    @Test
    void testInitAggregateSummary_shouldCreateAndSaveSummaryForYear() {
        // Arrange
        int year = 2025;

        // Act
        checkInProjectAggregateYearlySummaryService.initAggregateSummary(year);

        // Assert
        ArgumentCaptor<CheckInProjectAggregateYearlySummary> captor = ArgumentCaptor.forClass(CheckInProjectAggregateYearlySummary.class);
        verify(checkInProjectAggregateYearlySummaryRepository).save(captor.capture());
        var saved = captor.getValue();

        assertEquals(year, saved.getYear());
        assertEquals(LocalDate.of(year, 1, 1), saved.getStartDate());
        assertEquals(LocalDate.of(year, 12, 31), saved.getEndDate());
        assertEquals(0, saved.getNoOfCheckIns());
        assertEquals(0, saved.getStreak());
    }
}