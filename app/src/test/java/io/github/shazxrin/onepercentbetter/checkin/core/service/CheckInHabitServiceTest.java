package io.github.shazxrin.onepercentbetter.checkin.core.service;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInHabit;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInHabitRepository;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInHabitService;
import io.github.shazxrin.onepercentbetter.habit.model.Habit;
import io.github.shazxrin.onepercentbetter.habit.service.HabitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInHabitServiceTest {

    @Mock
    private CheckInHabitRepository checkInHabitRepository;

    @Mock
    private HabitService habitService;

    @InjectMocks
    private CheckInHabitService checkInHabitService;

    @Captor
    private ArgumentCaptor<CheckInHabit> checkInHabitCaptor;

    @Test
    void testCheckIn_shouldCreateAndSaveAddCheckInHabit() {
        // Given
        long habitId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 15);
        int amount = 30;
        String notes = "Completed 30 minutes of reading";

        Habit mockHabit = new Habit("Reading", "Read a book daily");
        mockHabit.setId(habitId);

        when(habitService.getHabitById(habitId)).thenReturn(mockHabit);
        when(checkInHabitRepository.save(any(CheckInHabit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInHabitService.addCheckIn(habitId, date, amount, notes);

        // Then
        verify(habitService, times(1)).getHabitById(habitId);
        verify(checkInHabitRepository, times(1)).save(checkInHabitCaptor.capture());

        CheckInHabit capturedCheckInHabit = checkInHabitCaptor.getValue();
        assertNotNull(capturedCheckInHabit);
        assertEquals(date, capturedCheckInHabit.getDate());
        assertEquals(amount, capturedCheckInHabit.getAmount());
        assertEquals(notes, capturedCheckInHabit.getNotes());
        assertEquals(mockHabit, capturedCheckInHabit.getHabit());
    }

    @Test
    void testCheckIn_withNullNotes_shouldCreateAndSaveAddCheckInHabitWithNullNotes() {
        // Given
        long habitId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 15);
        int amount = 30;
        String notes = null;

        Habit mockHabit = new Habit("Reading", "Read a book daily");
        mockHabit.setId(habitId);

        when(habitService.getHabitById(habitId)).thenReturn(mockHabit);
        when(checkInHabitRepository.save(any(CheckInHabit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInHabitService.addCheckIn(habitId, date, amount, notes);

        // Then
        verify(habitService, times(1)).getHabitById(habitId);
        verify(checkInHabitRepository, times(1)).save(checkInHabitCaptor.capture());

        CheckInHabit capturedCheckInHabit = checkInHabitCaptor.getValue();
        assertNotNull(capturedCheckInHabit);
        assertEquals(date, capturedCheckInHabit.getDate());
        assertEquals(amount, capturedCheckInHabit.getAmount());
        assertEquals(notes, capturedCheckInHabit.getNotes());
        assertEquals(mockHabit, capturedCheckInHabit.getHabit());
    }
}