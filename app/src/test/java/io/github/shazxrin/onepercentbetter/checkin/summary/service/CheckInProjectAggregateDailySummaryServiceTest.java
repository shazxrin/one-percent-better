package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectAggregateDailySummaryRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectAggregateDailySummaryServiceTest {
    @Mock
    private CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;

    @Mock
    private CheckInProjectRepository checkInProjectRepository;
    
    @InjectMocks
    private CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;

    @Test
    void testGetAggregateSummary_whenPresent_shouldReturnSummary() {
        // Given
        LocalDate date = LocalDate.of(2025, 7, 30);
        CheckInProjectAggregateDailySummary summary = new CheckInProjectAggregateDailySummary(date, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(date))
            .thenReturn(Optional.of(summary));

        // When
        CheckInProjectAggregateDailySummary result = checkInProjectAggregateDailySummaryService.getAggregateSummary(date);

        // Then
        assertEquals(summary, result);
        verify(checkInProjectAggregateDailySummaryRepository).findByDate(date);
    }

    @Test
    void testGetAggregateSummary_whenNotPresent_shouldThrowException() {
        // Given
        LocalDate date = LocalDate.of(2025, 7, 30);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(date))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalStateException.class, () -> checkInProjectAggregateDailySummaryService.getAggregateSummary(date));
    }

    @Test
    void testCalculateAggregateSummary_whenPreviousHasStreakAndCurrentHasCheckIns_shouldContinueStreak() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDate, 2, 3);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDate))
            .thenReturn(Optional.of(currentSummary));

        when(checkInProjectRepository.countByDateTimeBetween(currentDate.atTime(LocalTime.MIN), currentDate.atTime(LocalTime.MAX)))
            .thenReturn(3);

        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate, true);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDate, savedSummary.getDate());
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(4, savedSummary.getStreak());
    }

    @Test
    void testCalculateAggregateSummary_whenPreviousHasStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDate, 2, 3);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        when(checkInProjectRepository.countByDateTimeBetween(currentDate.atTime(LocalTime.MIN), currentDate.atTime(LocalTime.MAX)))
            .thenReturn(0);
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate, true);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDate, savedSummary.getDate());
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateAggregateSummary_whenPreviousHasNoStreakAndCurrentHasCheckIns_shouldStartStreakForCurrent() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        when(checkInProjectRepository.countByDateTimeBetween(currentDate.atTime(LocalTime.MIN), currentDate.atTime(LocalTime.MAX)))
            .thenReturn(3);
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate, true);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDate, savedSummary.getDate());
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateAggregateSummary_whenPreviousHasNoStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        when(checkInProjectRepository.countByDateTimeBetween(currentDate.atTime(LocalTime.MIN), currentDate.atTime(LocalTime.MAX)))
            .thenReturn(0);
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate, true);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDate, savedSummary.getDate());
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }

    @Test
    void testCalculateAggregateSummary_whenPreviousNotPresent_shouldThrowException() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalStateException.class, () -> checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate, true));
    }

    @Test
    void testCalculateAggregateSummary_whenCurrentNotPresent_shouldThrowException() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDate))
            .thenReturn(Optional.empty());

        LocalDate previousDate = currentDate.minusDays(1);
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.of(previousSummary));

        // When & Then
        assertThrows(IllegalStateException.class, () -> checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate, true));
    }


    @Test
    void testInitAggregateSummaries_shouldCreateSummariesForEntireYear() {
        // Given
        int daysInYear = LocalDate.now().lengthOfYear();
        
        // When
        checkInProjectAggregateDailySummaryService.initAggregateSummaries();
        
        // Then
        ArgumentCaptor<List<CheckInProjectAggregateDailySummary>> summariesCaptor = 
            ArgumentCaptor.forClass(List.class);
        verify(checkInProjectAggregateDailySummaryRepository).saveAll(summariesCaptor.capture());
        
        List<CheckInProjectAggregateDailySummary> savedSummaries = summariesCaptor.getValue();
        assertEquals(daysInYear, savedSummaries.size());
    }
}
