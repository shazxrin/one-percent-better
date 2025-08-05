package io.github.shazxrin.onepercentbetter.checkin.summary.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.shazxrin.onepercentbetter.common.BaseRepositoryTest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.project.model.Project;

@DataJpaTest
public class CheckInProjectDailySummaryRepositoryTest extends BaseRepositoryTest {
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

    @Test
    void testFindAllByDate_whenHasDate_shouldReturnSummaries() {
        // Given
        LocalDate date = LocalDate.of(2024, 6, 5);

        Project project1 = new Project("Test Project 10");
        entityManager.persist(project1);
        Project project2 = new Project("Test Project 11");
        entityManager.persist(project2);
        Project projectOther = new Project("Test Project 12");
        entityManager.persist(projectOther);

        entityManager.flush();

        CheckInProjectDailySummary summary1 = new CheckInProjectDailySummary();
        summary1.setProject(project1);
        summary1.setDate(date);
        entityManager.persist(summary1);

        CheckInProjectDailySummary summary2 = new CheckInProjectDailySummary();
        summary2.setProject(project2);
        summary2.setDate(date);
        entityManager.persist(summary2);

        CheckInProjectDailySummary summaryOtherDate = new CheckInProjectDailySummary();
        summaryOtherDate.setProject(projectOther);
        summaryOtherDate.setDate(LocalDate.of(2024, 6, 6));
        entityManager.persist(summaryOtherDate);

        entityManager.flush();

        // When
        List<CheckInProjectDailySummary> found = checkInProjectDailySummaryRepository.findAllByDate(date);

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(s -> s.getProject().getId().equals(project1.getId())));
        assertTrue(found.stream().anyMatch(s -> s.getProject().getId().equals(project2.getId())));
    }
}