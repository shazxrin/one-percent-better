package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectWeeklySummary;
import io.github.shazxrin.onepercentbetter.configuration.RepositoryTestConfiguration;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Import(RepositoryTestConfiguration.class)
@DataJpaTest
public class CheckInProjectWeeklySummaryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckInProjectWeeklySummaryRepository repository;

    @Test
    void testFindByProjectIdAndYearAndWeekNo_whenExists_shouldReturnSummary() {
        // Given
        Project project = new Project("Test Project");
        entityManager.persistAndFlush(project);

        int year = 2025;
        LocalDate start = LocalDate.of(2025, 7, 28).with(DayOfWeek.MONDAY);
        int weekNo = start.get(java.time.temporal.WeekFields.of(DayOfWeek.MONDAY, 4).weekOfYear());
        LocalDate end = start.plusDays(6);

        CheckInProjectWeeklySummary summary = new CheckInProjectWeeklySummary(year, weekNo, start, end, 0, 0, project);
        summary.setTypeDistribution(new LinkedHashMap<>());
        summary.setHourDistribution(new LinkedHashMap<>());
        summary.setDayDistribution(new LinkedHashMap<>());
        entityManager.persistAndFlush(summary);

        // When
        Optional<CheckInProjectWeeklySummary> found = repository.findByProjectIdAndYearAndWeekNo(project.getId(), year, weekNo);

        // Then
        assertTrue(found.isPresent());
        assertEquals(project.getId(), found.get().getProject().getId());
        assertEquals(weekNo, found.get().getWeekNo());
        assertEquals(year, found.get().getYear());
    }

    @Test
    void testFindByProjectIdAndYearAndWeekNo_whenNotExists_shouldReturnEmpty() {
        // When
        Optional<CheckInProjectWeeklySummary> found = repository.findByProjectIdAndYearAndWeekNo(999L, 2025, 1);
        // Then
        assertTrue(found.isEmpty());
    }
}
