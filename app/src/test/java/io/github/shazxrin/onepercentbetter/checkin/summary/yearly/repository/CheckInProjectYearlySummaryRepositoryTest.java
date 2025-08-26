package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectYearlySummary;
import io.github.shazxrin.onepercentbetter.configuration.RepositoryTestConfiguration;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Import(RepositoryTestConfiguration.class)
@DataJpaTest
public class CheckInProjectYearlySummaryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckInProjectYearlySummaryRepository checkInProjectYearlySummaryRepository;

    @Test
    void testFindByProjectIdAndYear_whenExists_shouldReturnSummary() {
        // Given
        Project project = new Project("Test Project");
        entityManager.persistAndFlush(project);

        int year = 2025;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        CheckInProjectYearlySummary summary = new CheckInProjectYearlySummary(year, start, end, 0, 0, project);
        entityManager.persistAndFlush(summary);

        // When
        Optional<CheckInProjectYearlySummary> found = checkInProjectYearlySummaryRepository.findByProjectIdAndYear(project.getId(), year);

        // Then
        assertTrue(found.isPresent());
        assertEquals(project.getId(), found.get().getProject().getId());
        assertEquals(year, found.get().getYear());
    }

    @Test
    void testFindByProjectIdAndYear_whenNotExists_shouldReturnEmpty() {
        // When
        Optional<CheckInProjectYearlySummary> found = checkInProjectYearlySummaryRepository.findByProjectIdAndYear(999L, 2025);
        
        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void testFindByProjectIdAndYearWithLock_whenExists_shouldReturnSummary() {
        // Given
        Project project = new Project("Test Project Lock");
        entityManager.persistAndFlush(project);

        int year = 2025;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        CheckInProjectYearlySummary summary = new CheckInProjectYearlySummary(year, start, end, 0, 0, project);
        entityManager.persistAndFlush(summary);

        // When
        Optional<CheckInProjectYearlySummary> found = checkInProjectYearlySummaryRepository.findByProjectIdAndYearWithLock(
            project.getId(), year);

        // Then
        assertTrue(found.isPresent());
        assertEquals(project.getId(), found.get().getProject().getId());
        assertEquals(year, found.get().getYear());
    }
}