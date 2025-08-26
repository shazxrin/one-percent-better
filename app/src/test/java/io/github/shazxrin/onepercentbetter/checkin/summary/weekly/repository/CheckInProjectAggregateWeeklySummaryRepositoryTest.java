package io.github.shazxrin.onepercentbetter.checkin.summary.weekly.repository;

import io.github.shazxrin.onepercentbetter.checkin.summary.weekly.model.CheckInProjectAggregateWeeklySummary;
import io.github.shazxrin.onepercentbetter.configuration.RepositoryTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(RepositoryTestConfiguration.class)
@DataJpaTest
public class CheckInProjectAggregateWeeklySummaryRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private CheckInProjectAggregateWeeklySummaryRepository repository;

    @Test
    void testFindByYearAndWeekNo_whenSeeded_shouldBePresent() {
        // Given
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int weekNo = now.get(WeekFields.of(DayOfWeek.MONDAY, 4).weekOfYear());

        // When
        Optional<CheckInProjectAggregateWeeklySummary> result = repository.findByYearAndWeekNo(year, weekNo);

        // Then
        assertThat(result).isPresent();
    }
}