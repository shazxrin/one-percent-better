package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectAggregateYearlySummary;
import io.github.shazxrin.onepercentbetter.configuration.RepositoryTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Import(RepositoryTestConfiguration.class)
@DataJpaTest
class CheckInProjectAggregateYearlySummaryRepositoryTest {

    @Autowired
    private CheckInProjectAggregateYearlySummaryRepository repository;

    @Test
    void testFindByYear_whenSummaryExists_shouldReturnSummary() {
        // Arrange
        int year = LocalDate.now().getYear();
        var startDate = LocalDate.of(year, 1, 1);
        var endDate = LocalDate.of(year, 12, 31);

        // Act
        var result = repository.findByYear(year);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(year, result.get().getYear());
        assertEquals(startDate, result.get().getStartDate());
        assertEquals(endDate, result.get().getEndDate());
    }

    @Test
    void testFindByYear_whenSummaryDoesNotExist_shouldReturnEmpty() {
        // Arrange
        int nonExistentYear = 2011;

        // Act
        var result = repository.findByYear(nonExistentYear);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByYearWithLock_whenSummaryExists_shouldReturnSummary() {
        // Arrange
        int year = LocalDate.now().getYear();

        // Act
        var result = repository.findByYearWithLock(year);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(year, result.get().getYear());
    }
}