package io.github.shazxrin.onepercentbetter.checkin.summary.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.shazxrin.onepercentbetter.configuration.RepositoryTestConfiguration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.springframework.context.annotation.Import;

@Import(RepositoryTestConfiguration.class)
@DataJpaTest
public class CheckInProjectDailySummaryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    @Test
    void testFindByProjectIdAndDate_whenHasProjectIdAndDate_shouldReturnSummary() {
        // Given
        Project project = new Project("Test Project 1");
        entityManager.persistAndFlush(project);

        LocalDate date = LocalDate.of(2024, 6, 1);
        CheckInProjectDailySummary summary = new CheckInProjectDailySummary();
        summary.setProject(project);
        summary.setDate(date);
        entityManager.persistAndFlush(summary);

        // When
        Optional<CheckInProjectDailySummary> found = checkInProjectDailySummaryRepository.findByProjectIdAndDate(project.getId(), date);

        // Then
        assertTrue(found.isPresent());
        assertEquals(project.getId(), found.get().getProject().getId());
        assertEquals(date, found.get().getDate());
    }

    @Test
    void testFindByProjectIdAndDate_whenHasNoProjectIdAndHasNoDate_shouldReturnEmpty() {
        // When
        Optional<CheckInProjectDailySummary> found = checkInProjectDailySummaryRepository.findByProjectIdAndDate(999L, LocalDate.of(2024, 6, 1));

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByProjectIdAndDate_whenHasProjectIdButHasNoDate_shouldReturnEmpty() {
        // Given
        Project project = new Project("Test Project 2");
        entityManager.persistAndFlush(project);

        LocalDate date = LocalDate.of(2024, 6, 2);
        CheckInProjectDailySummary summary = new CheckInProjectDailySummary();
        summary.setProject(project);
        summary.setDate(date);
        entityManager.persistAndFlush(summary);

        // When
        Optional<CheckInProjectDailySummary> found = checkInProjectDailySummaryRepository.findByProjectIdAndDate(project.getId(), LocalDate.of(2024, 6, 3));

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByProjectIdAndDate_whenHasNoProjectIdButHasDate_shouldReturnEmpty() {
        // Given
        Project project = new Project("Test Project 3");
        entityManager.persistAndFlush(project);

        LocalDate date = LocalDate.of(2024, 6, 4);
        CheckInProjectDailySummary summary = new CheckInProjectDailySummary();
        summary.setProject(project);
        summary.setDate(date);
        entityManager.persistAndFlush(summary);

        // When
        Optional<CheckInProjectDailySummary> found = checkInProjectDailySummaryRepository.findByProjectIdAndDate(999L, date);

        // Then
        assertTrue(found.isEmpty());
    }
}