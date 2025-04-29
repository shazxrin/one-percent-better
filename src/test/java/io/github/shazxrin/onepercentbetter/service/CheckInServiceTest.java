package io.github.shazxrin.onepercentbetter.service;

import io.github.shazxrin.onepercentbetter.model.CheckIn;
import io.github.shazxrin.onepercentbetter.model.Project;
import io.github.shazxrin.onepercentbetter.repository.CheckInRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInServiceTest {

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private GitHubService gitHubService;

    @InjectMocks
    private CheckInService checkInService;

    @Captor
    private ArgumentCaptor<CheckIn> checkInCaptor;

    private LocalDate today;
    private LocalDate yesterday;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        yesterday = today.minusDays(1);
    }

    @Test
    void shouldCreateNewCheckInWithOneStreakWhenCommitsExistAndNoYesterdayCheckIn() {
        // Given
        Project project = new Project(null, "shazxrin", "one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountTodayForRepository("shazxrin", "one-percent-better")).thenReturn(3);
        when(checkInRepository.findByDate(yesterday)).thenReturn(null);
        when(checkInRepository.findByDate(today)).thenReturn(null);

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(today, savedCheckIn.getDate());
        assertEquals(3, savedCheckIn.getCount());
        assertEquals(1, savedCheckIn.getStreak());
    }

    @Test
    void shouldContinueStreakWhenCommitsExistAndYesterdayCheckInExists() {
        // Given
        Project project = new Project(null, "shazxrin", "one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountTodayForRepository("shazxrin", "one-percent-better")).thenReturn(5);

        CheckIn yesterdayCheckIn = new CheckIn(null, yesterday, 2, 3);
        when(checkInRepository.findByDate(yesterday)).thenReturn(yesterdayCheckIn);
        when(checkInRepository.findByDate(today)).thenReturn(null);

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(today, savedCheckIn.getDate());
        assertEquals(5, savedCheckIn.getCount());
        assertEquals(4, savedCheckIn.getStreak());
    }

    @Test
    void shouldNotContinueStreakWhenNoCommitsExistToday() {
        // Given
        Project project = new Project(null, "shazxrin", "one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountTodayForRepository("shazxrin", "one-percent-better")).thenReturn(0);

        CheckIn yesterdayCheckIn = new CheckIn(null, yesterday, 2, 3);
        when(checkInRepository.findByDate(yesterday)).thenReturn(yesterdayCheckIn);
        when(checkInRepository.findByDate(today)).thenReturn(null);

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(today, savedCheckIn.getDate());
        assertEquals(0, savedCheckIn.getCount());
        assertEquals(0, savedCheckIn.getStreak());
    }

    @Test
    void shouldCreateZeroStreakWhenNoCommitsAndNoYesterdayCheckIn() {
        // Given
        Project project = new Project(null, "shazxrin", "one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountTodayForRepository("shazxrin", "one-percent-better")).thenReturn(0);
        when(checkInRepository.findByDate(yesterday)).thenReturn(null);
        when(checkInRepository.findByDate(today)).thenReturn(null);

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(today, savedCheckIn.getDate());
        assertEquals(0, savedCheckIn.getCount());
        assertEquals(0, savedCheckIn.getStreak());
    }

    @Test
    void shouldUpdateExistingCheckInWhenTodaysCheckInExists() {
        // Given
        Project project = new Project(null, "shazxrin", "one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountTodayForRepository("shazxrin", "one-percent-better")).thenReturn(7);

        CheckIn existingCheckIn = new CheckIn("check-in-id", today, 3, 2);
        CheckIn yesterdayCheckIn = new CheckIn("check-in-id", today, 3, 1);
        when(checkInRepository.findByDate(today)).thenReturn(existingCheckIn);
        when(checkInRepository.findByDate(yesterday)).thenReturn(yesterdayCheckIn);

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(existingCheckIn.getId(), savedCheckIn.getId());
        assertEquals(today, savedCheckIn.getDate());
        assertEquals(7, savedCheckIn.getCount());
        assertEquals(2, savedCheckIn.getStreak());
    }

    @Test
    void shouldHandleMultipleProjectsAndSumCommitCounts() {
        // Given
        Project project1 = new Project(null, "shazxrin", "project1");
        Project project2 = new Project(null, "shazxrin", "project2");
        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));

        when(gitHubService.getCommitCountTodayForRepository("shazxrin", "project1")).thenReturn(3);
        when(gitHubService.getCommitCountTodayForRepository("shazxrin", "project2")).thenReturn(4);

        when(checkInRepository.findByDate(yesterday)).thenReturn(null);
        when(checkInRepository.findByDate(today)).thenReturn(null);

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(today, savedCheckIn.getDate());
        assertEquals(7, savedCheckIn.getCount());
        assertEquals(1, savedCheckIn.getStreak());
    }

    @Test
    void shouldHandleNoProjectsCase() {
        // Given
        when(projectService.getAllProjects()).thenReturn(Collections.emptyList());
        when(checkInRepository.findByDate(yesterday)).thenReturn(null);
        when(checkInRepository.findByDate(today)).thenReturn(null);

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(today, savedCheckIn.getDate());
        assertEquals(0, savedCheckIn.getCount());
        assertEquals(0, savedCheckIn.getStreak());
    }
}
