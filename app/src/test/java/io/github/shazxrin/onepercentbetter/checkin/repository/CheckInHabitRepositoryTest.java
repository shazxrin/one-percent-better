package io.github.shazxrin.onepercentbetter.checkin.repository;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInHabit;
import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CheckInHabitRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckInHabitRepository checkInHabitRepository;

    @Test
    void testFindAllByDate_whenDateExists_shouldReturnCheckInHabits() {
        // Given
        LocalDate testDate = LocalDate.of(2023, 1, 15);
        
        // Create a habit
        Habit habit1 = new Habit("Reading", "Read a book daily");
        entityManager.persistAndFlush(habit1);
        
        Habit habit2 = new Habit("Exercise", "Go to the gym");
        entityManager.persistAndFlush(habit2);
        
        // Create check-ins for the habits on the test date
        CheckInHabit checkInHabit1 = new CheckInHabit(testDate, 30, "Read 30 pages", habit1);
        entityManager.persistAndFlush(checkInHabit1);
        
        CheckInHabit checkInHabit2 = new CheckInHabit(testDate, 45, "Exercised for 45 minutes", habit2);
        entityManager.persistAndFlush(checkInHabit2);
        
        // Create a check-in for a different date
        CheckInHabit checkInHabit3 = new CheckInHabit(testDate.plusDays(1), 20, "Read 20 pages", habit1);
        entityManager.persistAndFlush(checkInHabit3);

        // When
        List<CheckInHabit> foundCheckInHabits = checkInHabitRepository.findAllByDate(testDate);

        // Then
        assertNotNull(foundCheckInHabits);
        assertEquals(2, foundCheckInHabits.size());
        
        // Verify the check-ins are for the correct date
        for (CheckInHabit checkInHabit : foundCheckInHabits) {
            assertEquals(testDate, checkInHabit.getDate());
        }
        
        // Verify the check-ins have the expected habits
        boolean hasHabit1 = false;
        boolean hasHabit2 = false;
        
        for (CheckInHabit checkInHabit : foundCheckInHabits) {
            if (checkInHabit.getHabit().getId().equals(habit1.getId())) {
                hasHabit1 = true;
                assertEquals(30, checkInHabit.getAmount());
                assertEquals("Read 30 pages", checkInHabit.getNotes());
            } else if (checkInHabit.getHabit().getId().equals(habit2.getId())) {
                hasHabit2 = true;
                assertEquals(45, checkInHabit.getAmount());
                assertEquals("Exercised for 45 minutes", checkInHabit.getNotes());
            }
        }
        
        assertTrue(hasHabit1);
        assertTrue(hasHabit2);
    }

    @Test
    void testFindAllByDate_whenNoMatchExists_shouldReturnEmptyList() {
        // Given
        LocalDate nonExistentDate = LocalDate.of(2099, 12, 31);

        // When
        List<CheckInHabit> foundCheckInHabits = checkInHabitRepository.findAllByDate(nonExistentDate);

        // Then
        assertNotNull(foundCheckInHabits);
        assertTrue(foundCheckInHabits.isEmpty());
    }
}