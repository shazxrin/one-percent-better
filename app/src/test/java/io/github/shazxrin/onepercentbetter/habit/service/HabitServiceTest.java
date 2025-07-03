package io.github.shazxrin.onepercentbetter.habit.service;

import io.github.shazxrin.onepercentbetter.habit.exception.HabitNotFoundException;
import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import io.github.shazxrin.onepercentbetter.habit.repository.HabitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private MainHabitService mainHabitService;

    @Test
    void testAddHabit_shouldSaveHabit() {
        // Given
        String name = "New Habit";
        String description = "Description of new habit";
        Habit expectedHabit = new Habit(name, description);
        when(habitRepository.save(any(Habit.class))).thenReturn(expectedHabit);

        // When
        mainHabitService.addHabit(name, description);

        // Then
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void testRemoveHabit_shouldDeleteHabit_whenHabitExists() {
        // Given
        Long habitId = 1L;
        when(habitRepository.existsById(habitId)).thenReturn(false);

        // When
        mainHabitService.removeHabit(habitId);

        // Then
        verify(habitRepository, times(1)).existsById(habitId);
        verify(habitRepository, times(1)).deleteById(habitId);
    }

    @Test
    void testRemoveHabit_shouldThrowException_whenHabitDoesNotExist() {
        // Given
        Long habitId = 1L;
        when(habitRepository.existsById(habitId)).thenReturn(true);

        // When & Then
        assertThrows(HabitNotFoundException.class, () -> mainHabitService.removeHabit(habitId));
        verify(habitRepository, times(1)).existsById(habitId);
        verify(habitRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetAllHabits_shouldReturnAllHabits() {
        // Given
        Habit habit1 = new Habit("Read", "Read a book daily");
        Habit habit2 = new Habit("Exercise", "Go to the gym");
        List<Habit> expectedHabits = Arrays.asList(habit1, habit2);
        when(habitRepository.findAll()).thenReturn(expectedHabits);

        // When
        List<Habit> actualHabits = mainHabitService.getAllHabits();

        // Then
        assertEquals(expectedHabits.size(), actualHabits.size());
        assertEquals(expectedHabits, actualHabits);
        verify(habitRepository, times(1)).findAll();
    }
}