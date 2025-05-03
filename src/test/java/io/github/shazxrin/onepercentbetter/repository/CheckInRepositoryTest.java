package io.github.shazxrin.onepercentbetter.repository;

import io.github.shazxrin.onepercentbetter.model.CheckIn;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
public class CheckInRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckInRepository checkInRepository;

    @Test
    void testFindByDate_whenDateExists_shouldReturnCheckIn() {
        // Given
        LocalDate testDate = LocalDate.of(2023, 1, 15);

        CheckIn checkIn = new CheckIn();
        checkIn.setDate(testDate);
        checkIn.setCount(7);
        checkIn.setStreak(2);
        entityManager.persistAndFlush(checkIn);

        CheckIn anotherCheckIn = new CheckIn();
        anotherCheckIn.setDate(testDate.plusDays(1));
        anotherCheckIn.setCount(8);
        anotherCheckIn.setStreak(3);
        entityManager.persistAndFlush(anotherCheckIn);

        // When
        CheckIn foundCheckIn = checkInRepository.findByDate(testDate);

        // Then
        assertNotNull(foundCheckIn);
        assertEquals(testDate, foundCheckIn.getDate());
        assertEquals(7, foundCheckIn.getCount());
        assertEquals(2, foundCheckIn.getStreak());
    }

    @Test
    void testFindByDate_whenNoMatchExists_shouldReturnNull() {
        // Given
        LocalDate nonExistentDate = LocalDate.of(2099, 12, 31);

        // When
        CheckIn foundCheckIn = checkInRepository.findByDate(nonExistentDate);

        // Then
        assertNull(foundCheckIn);
    }
}
