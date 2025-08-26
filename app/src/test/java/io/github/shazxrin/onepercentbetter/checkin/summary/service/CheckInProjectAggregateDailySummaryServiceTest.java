package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectAggregateDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectAggregateDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectAggregateDailySummaryServiceTest {
    @Mock
    private CheckInProjectService checkInProjectService;

    @Mock
    private CheckInProjectAggregateDailySummaryRepository checkInProjectAggregateDailySummaryRepository;

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
    void testCalculateAggregateSummaryForDate_whenPreviousHasStreakAndCurrentHasCheckIns_shouldContinueStreak() {
        // Given
        LocalDateTime currentDateTime = LocalDateTime.of(2025, 7, 30, 12, 0);
        LocalDateTime previousDate = currentDateTime.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDate.toLocalDate(), 2, 3);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate.toLocalDate()))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDateTime.toLocalDate(), 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDateTime.toLocalDate()))
            .thenReturn(Optional.of(currentSummary));

        Project project = new Project("Project");
        project.setId(1L);

        CheckInProject checkInProject1 = new CheckInProject(currentDateTime, "a1", "feat", "message", project);
        CheckInProject checkInProject2 = new CheckInProject(currentDateTime, "a2", "feat", "message", project);
        CheckInProject checkInProject3 = new CheckInProject(currentDateTime, "a3", "feat", "message", project);
        when(checkInProjectService.getAllCheckIns(currentDateTime.toLocalDate()))
            .thenReturn(List.of(checkInProject1, checkInProject2, checkInProject3));

        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummaryForDate(currentDateTime.toLocalDate(), true);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDateTime.toLocalDate(), savedSummary.getDate());
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(4, savedSummary.getStreak());
        assertEquals(3, savedSummary.getTypeDistribution().get("feat"));
        assertEquals(3, savedSummary.getHourDistribution().get(String.valueOf(currentDateTime.getHour())));
    }

    @Test
    void testCalculateAggregateSummaryForDate_whenPreviousHasStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        // Given
        LocalDateTime currentDateTime = LocalDateTime.of(2025, 7, 30, 12, 0);
        LocalDateTime previousDateTime = currentDateTime.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDateTime.toLocalDate(), 2, 3);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDateTime.toLocalDate()))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDateTime.toLocalDate(), 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDateTime.toLocalDate()))
            .thenReturn(Optional.of(currentSummary));

        when(checkInProjectService.getAllCheckIns(currentDateTime.toLocalDate()))
            .thenReturn(List.of());
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummaryForDate(currentDateTime.toLocalDate(), true);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDateTime.toLocalDate(), savedSummary.getDate());
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
        assertNull(savedSummary.getTypeDistribution().get("feat"));
        assertEquals(0, savedSummary.getHourDistribution().get(String.valueOf(currentDateTime.getHour())));
    }
    
    @Test
    void testCalculateAggregateSummaryForDate_whenPreviousHasNoStreakAndCurrentHasCheckIns_shouldStartStreakForCurrent() {
        // Given
        LocalDateTime currentDateTime = LocalDateTime.of(2025, 7, 30, 12, 0);
        LocalDateTime previousDateTime = currentDateTime.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDateTime.toLocalDate(), 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDateTime.toLocalDate()))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDateTime.toLocalDate(), 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDateTime.toLocalDate()))
            .thenReturn(Optional.of(currentSummary));

        Project project = new Project("Project");
        project.setId(1L);

        CheckInProject checkInProject1 = new CheckInProject(currentDateTime, "a1", "feat", "message", project);
        CheckInProject checkInProject2 = new CheckInProject(currentDateTime, "a2", "feat", "message", project);
        CheckInProject checkInProject3 = new CheckInProject(currentDateTime, "a3", "feat", "message", project);
        when(checkInProjectService.getAllCheckIns(currentDateTime.toLocalDate()))
            .thenReturn(List.of(checkInProject1, checkInProject2, checkInProject3));
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummaryForDate(currentDateTime.toLocalDate(), true);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDateTime.toLocalDate(), savedSummary.getDate());
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
        assertEquals(3, savedSummary.getTypeDistribution().get("feat"));
        assertEquals(3, savedSummary.getHourDistribution().get(String.valueOf(currentDateTime.getHour())));
    }
    
    @Test
    void testCalculateAggregateSummaryForDate_whenPreviousHasNoStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        // Given
        LocalDateTime currentDateTime = LocalDateTime.of(2025, 7, 30, 12, 0);
        LocalDateTime previousDateTime = currentDateTime.minusDays(1);
        
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDateTime.toLocalDate(), 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDateTime.toLocalDate()))
            .thenReturn(Optional.of(previousSummary));
        
        CheckInProjectAggregateDailySummary currentSummary = new CheckInProjectAggregateDailySummary(currentDateTime.toLocalDate(), 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDateTime.toLocalDate()))
            .thenReturn(Optional.of(currentSummary));

        when(checkInProjectService.getAllCheckIns(currentDateTime.toLocalDate()))
            .thenReturn(List.of());
        
        // When
        checkInProjectAggregateDailySummaryService.calculateAggregateSummaryForDate(currentDateTime.toLocalDate(), true);
        
        // Then
        ArgumentCaptor<CheckInProjectAggregateDailySummary> summaryCaptor = 
            ArgumentCaptor.forClass(CheckInProjectAggregateDailySummary.class);
        verify(checkInProjectAggregateDailySummaryRepository).save(summaryCaptor.capture());
        
        CheckInProjectAggregateDailySummary savedSummary = summaryCaptor.getValue();
        assertEquals(currentDateTime.toLocalDate(), savedSummary.getDate());
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }

    @Test
    void testCalculateAggregateSummaryForDate_whenPreviousNotPresent_shouldThrowException() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        LocalDate previousDate = currentDate.minusDays(1);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalStateException.class, () -> checkInProjectAggregateDailySummaryService.calculateAggregateSummaryForDate(currentDate, true));
    }

    @Test
    void testCalculateAggregateSummaryForDate_whenCurrentNotPresent_shouldThrowException() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 7, 30);
        when(checkInProjectAggregateDailySummaryRepository.findByDateWithLock(currentDate))
            .thenReturn(Optional.empty());

        LocalDate previousDate = currentDate.minusDays(1);
        CheckInProjectAggregateDailySummary previousSummary = new CheckInProjectAggregateDailySummary(previousDate, 0, 0);
        when(checkInProjectAggregateDailySummaryRepository.findByDate(previousDate))
            .thenReturn(Optional.of(previousSummary));

        // When & Then
        assertThrows(IllegalStateException.class, () -> checkInProjectAggregateDailySummaryService.calculateAggregateSummaryForDate(currentDate, true));
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
