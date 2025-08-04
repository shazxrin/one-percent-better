package io.github.shazxrin.onepercentbetter.checkin.summary.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CheckInProjectAggregateDailySummaryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;

    @Test
    void testFindByDate_whenSummaryForDateIsPresent_shouldBePresent() {
        // Given
        LocalDate testDate = LocalDate.of(2025, 7, 31);
        CheckInProjectAggregateDailySummary summary = new CheckInProjectAggregateDailySummary(testDate, 5, 3);
        entityManager.persist(summary);
        entityManager.flush();

        // When
        Optional<CheckInProjectAggregateDailySummary> result = checkInProjectAggregateDailySummaryRepository.findByDate(testDate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDate()).isEqualTo(testDate);
        assertThat(result.get().getNoOfCheckIns()).isEqualTo(5);
        assertThat(result.get().getStreak()).isEqualTo(3);
    }

    @Test
    void testFindByDate_whenSummaryForDateIsNotPresent_shouldNotBePresent() {
        // Given
        LocalDate nonExistentDate = LocalDate.of(2025, 8, 1);

        // When
        Optional<CheckInProjectAggregateDailySummary> result = checkInProjectAggregateDailySummaryRepository.findByDate(nonExistentDate);

        // Then
        assertThat(result).isEmpty();
    }
}