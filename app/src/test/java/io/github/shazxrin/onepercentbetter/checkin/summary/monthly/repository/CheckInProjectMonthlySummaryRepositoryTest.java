package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectMonthlySummary;
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
public class CheckInProjectMonthlySummaryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckInProjectMonthlySummaryRepository checkInProjectMonthlySummaryRepository;

    @Test
    void testFindByProjectIdAndYearAndMonthNo_whenExists_shouldReturnSummary() {
        // Given
        Project project = new Project("Test Project");
        entityManager.persistAndFlush(project);

        int year = 2025;
        int monthNo = 8;
        LocalDate start = LocalDate.of(2025, 8, 1);
        LocalDate end = LocalDate.of(2025, 8, 31);

        CheckInProjectMonthlySummary summary = new CheckInProjectMonthlySummary(year, monthNo, start, end, 0, 0, project);
        entityManager.persistAndFlush(summary);

        // When
        Optional<CheckInProjectMonthlySummary> found = checkInProjectMonthlySummaryRepository.findByProjectIdAndYearAndMonthNo(project.getId(), year, monthNo);

        // Then
        assertTrue(found.isPresent());
        assertEquals(project.getId(), found.get().getProject().getId());
        assertEquals(monthNo, found.get().getMonthNo());
        assertEquals(year, found.get().getYear());
    }

    @Test
    void testFindByProjectIdAndYearAndMonthNo_whenNotExists_shouldReturnEmpty() {
        // When
        Optional<CheckInProjectMonthlySummary> found = checkInProjectMonthlySummaryRepository.findByProjectIdAndYearAndMonthNo(999L, 2025, 8);
        
        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void testFindByProjectIdAndYearAndMonthNoWithLock_whenExists_shouldReturnSummary() {
        // Given
        Project project = new Project("Test Project Lock");
        entityManager.persistAndFlush(project);

        int year = 2025;
        int monthNo = 8;
        LocalDate start = LocalDate.of(2025, 8, 1);
        LocalDate end = LocalDate.of(2025, 8, 31);

        CheckInProjectMonthlySummary summary = new CheckInProjectMonthlySummary(year, monthNo, start, end, 0, 0, project);
        entityManager.persistAndFlush(summary);

        // When
        Optional<CheckInProjectMonthlySummary> found = checkInProjectMonthlySummaryRepository.findByProjectIdAndYearAndMonthNoWithLock(
            project.getId(), year, monthNo);

        // Then
        assertTrue(found.isPresent());
        assertEquals(project.getId(), found.get().getProject().getId());
        assertEquals(monthNo, found.get().getMonthNo());
        assertEquals(year, found.get().getYear());
    }
}