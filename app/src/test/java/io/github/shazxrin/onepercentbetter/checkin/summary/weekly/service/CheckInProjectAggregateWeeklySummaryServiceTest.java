package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectAggregateWeeklySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.repository.CheckInProjectAggregateWeeklySummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectAggregateWeeklySummaryServiceTest {
    @Mock
    private CheckInProjectAggregateWeeklySummaryRepository repository;

    @Mock
    private CheckInProjectService checkInProjectService;

    @InjectMocks
    private CheckInProjectAggregateWeeklySummaryService service;

    @Test
    void testGetAggregateSummary_whenPresent_shouldReturnSummary() {
        // Arrange
        int year = 2025;
        int week = 32;
        var summary = new CheckInProjectAggregateWeeklySummary(year, week, LocalDate.of(2025,8,4), LocalDate.of(2025,8,10), 0, 0);
        when(repository.findByYearAndWeekNo(year, week)).thenReturn(Optional.of(summary));

        // Act
        var result = service.getAggregateSummary(year, week);

        // Assert
        assertEquals(summary, result);
        verify(repository).findByYearAndWeekNo(year, week);
    }

    @Test
    void testGetAggregateSummary_whenNotPresent_shouldThrowException() {
        // Arrange
        when(repository.findByYearAndWeekNo(2025, 1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> service.getAggregateSummary(2025, 1));
    }

    @Test
    void testCalculateAggregateSummaryForWeek_shouldComputeAndSaveDistributions() {
        // Arrange
        int year = 2025;
        int week = 31;
        LocalDate start = LocalDate.of(2025, 7, 28);
        LocalDate end = start.plusDays(6);
        var summary = new CheckInProjectAggregateWeeklySummary(year, week, start, end, 0, 0);
        when(repository.findByYearAndWeekNoWithLock(year, week)).thenReturn(Optional.of(summary));

        var p1 = new CheckInProject();
        p1.setDateTime(LocalDateTime.of(2025,7,28,12,0));
        p1.setType("feat");
        var proj = new io.github.shazxrin.onepercentbetter.project.model.Project();
        proj.setName("Alpha");
        p1.setProject(proj);

        var p2 = new CheckInProject();
        p2.setDateTime(LocalDateTime.of(2025,7,29,12,0));
        p2.setType(null); // unknown type handling
        p2.setProject(proj);

        when(checkInProjectService.getAllCheckInsBetween(start, end)).thenReturn(List.of(p1, p2));

        // Act
        service.calculateAggregateSummaryForWeek(year, week);

        // Assert
        ArgumentCaptor<CheckInProjectAggregateWeeklySummary> captor = ArgumentCaptor.forClass(CheckInProjectAggregateWeeklySummary.class);
        verify(repository).save(captor.capture());
        var saved = captor.getValue();

        assertEquals(2, saved.getNoOfCheckIns());
        assertTrue(saved.getStreak() >= 1); // two consecutive days streak -> should be at least 2, but allow utility specifics
        assertEquals(1, saved.getTypeDistribution().getOrDefault("feat", 0));
        assertEquals(1, saved.getTypeDistribution().getOrDefault("unknown", 0));
        assertEquals(2, saved.getHourDistribution().getOrDefault("12", 0));
        assertEquals(2, saved.getProjectDistribution().getOrDefault("Alpha", 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault(DayOfWeek.MONDAY.toString(), 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault(DayOfWeek.TUESDAY.toString(), 0));
    }

    @Test
    void testAddCheckInToAggregateSummary_shouldIncrementCountsAndRecalculateStreak() {
        // Arrange
        var project = new io.github.shazxrin.onepercentbetter.project.model.Project();
        project.setName("Beta");

        var checkIn = new CheckInProject();
        checkIn.setProject(project);
        checkIn.setType("chore");
        var dt = LocalDateTime.of(2025, 8, 1, 10, 0);
        checkIn.setDateTime(dt);

        when(checkInProjectService.getCheckIn(123L)).thenReturn(Optional.of(checkIn));

        int year = dt.getYear();
        int weekNo = dt.get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfYear());
        var start = dt.toLocalDate().with(DayOfWeek.MONDAY);
        var end = start.plusDays(6);
        var existing = new CheckInProjectAggregateWeeklySummary(year, weekNo, start, end, 0, 0);

        when(repository.findByYearAndWeekNo(year, weekNo)).thenReturn(Optional.of(existing));

        // Act
        service.addCheckInToAggregateSummary(123L);

        // Assert
        ArgumentCaptor<CheckInProjectAggregateWeeklySummary> captor = ArgumentCaptor.forClass(CheckInProjectAggregateWeeklySummary.class);
        verify(repository).save(captor.capture());
        var saved = captor.getValue();

        assertEquals(1, saved.getNoOfCheckIns());
        assertEquals(1, saved.getTypeDistribution().getOrDefault("chore", 0));
        assertEquals(1, saved.getHourDistribution().getOrDefault("10", 0));
        assertEquals(1, saved.getProjectDistribution().getOrDefault("Beta", 0));
        assertEquals(1, saved.getDayDistribution().getOrDefault(dt.getDayOfWeek().toString(), 0));
        assertTrue(saved.getStreak() >= 1);
    }
}