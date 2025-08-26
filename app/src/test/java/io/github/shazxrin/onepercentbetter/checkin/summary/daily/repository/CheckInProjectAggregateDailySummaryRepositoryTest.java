package io.github.shazxrin.onepercentbetter.checkin.summary.daily.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.daily.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.configuration.RepositoryTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.assertj.core.api.Assertions.assertThat;

@Import(RepositoryTestConfiguration.class)
@DataJpaTest
public class CheckInProjectAggregateDailySummaryRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;

    @Test
    void testFindByDate_whenSeeded_shouldBePresent() {
        // Given
        LocalDate testDate = LocalDate.now();

        // When
        Optional<CheckInProjectAggregateDailySummary> result = checkInProjectAggregateDailySummaryRepository.findByDate(testDate);

        // Then
        assertThat(result).isPresent();
    }
}