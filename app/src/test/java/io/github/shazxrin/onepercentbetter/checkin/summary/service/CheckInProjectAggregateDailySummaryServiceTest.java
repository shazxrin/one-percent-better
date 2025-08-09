package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectAggregateDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectAggregateDailySummaryServiceTest {
    
    @Mock
    private CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;
    
    @Mock
    private CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;
    
    @InjectMocks
    private CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;
    
    
    @Test
    void testCalculateAggregateSummary_whenPreviousHasStreakAndCurrentHasCheckIns_shouldContinueStreak() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDate, 2, 3);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        CheckInProjectDailySummary dailySummary1 = new CheckInProjectDailySummary();
        dailySummary1.setNoOfCheckIns(2);
        CheckInProjectDailySummary dailySummary2 = new CheckInProjectDailySummary();
        dailySummary2.setNoOfCheckIns(1);
        when(checkInProjectDailySummaryRepository.findAllByDate(currentDate))
            .thenReturn(List.of(dailySummary1, dailySummary2));
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate);
        
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
        when(checkInProjectAggregateDailySummaryRepository.findByDate(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        when(checkInProjectDailySummaryRepository.findAllByDate(currentDate))
            .thenReturn(Collections.emptyList());
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate);
        
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
        when(checkInProjectAggregateDailySummaryRepository.findByDate(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        CheckInProjectDailySummary dailySummary = new CheckInProjectDailySummary();
        dailySummary.setNoOfCheckIns(3);
        when(checkInProjectDailySummaryRepository.findAllByDate(currentDate))
            .thenReturn(List.of(dailySummary));
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate);
        
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
        when(checkInProjectAggregateDailySummaryRepository.findByDate(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        when(checkInProjectDailySummaryRepository.findAllByDate(currentDate))
            .thenReturn(Collections.emptyList());
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate);
        
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
    void testCalculateAggregateSummary_whenPreviousIsMissingAndCurrentHaveCheckIns_shouldStartStreakForCurrent() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.empty());
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        CheckInProjectDailySummary dailySummary = new CheckInProjectDailySummary();
        dailySummary.setNoOfCheckIns(2);
        when(checkInProjectDailySummaryRepository.findAllByDate(currentDate))
            .thenReturn(List.of(dailySummary));
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDate, savedSummary.getDate());
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
    }

    @Test
    void testCalculateAggregateSummary_whenPreviousIsMissingAndCurrentHaveNoCheckIns_shouldHaveZeroStreakForCurrent() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.empty());
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(currentDate))
            .thenReturn(Optional.of(currentSummary));
        
        when(checkInProjectDailySummaryRepository.findAllByDate(currentDate))
            .thenReturn(Collections.emptyList());
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(currentDate);
        
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
