package io.github.shazxrin.onepercentbetter.checkin.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.checkin.repository.CheckInRepository;
import io.github.shazxrin.onepercentbetter.github.service.GitHubService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    @Test
    void testCheckInToday_whenTodayHasCommitsNoCheckInAndYesterdayNoCheckIn_shouldCreateNewCheckInWithOneStreak() {
        // Given
        Project project = new Project("shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", LocalDate.now())).thenReturn(3);
        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(null);
        when(checkInRepository.findByDate(TODAY)).thenReturn(null);
        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(TODAY, savedCheckIn.getDate());
        assertEquals(3, savedCheckIn.getCount());
        assertEquals(1, savedCheckIn.getStreak());
    }

    @Test
    void testCheckInToday_whenTodayHasCommitsNoCheckInAndYesterdayHasCheckIn_shouldCreateNewCheckInContinueStreak() {
        // Given
        Project project = new Project("shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", LocalDate.now())).thenReturn(5);

        CheckIn yesterdayCheckIn = new CheckIn(null, YESTERDAY, 2, 3);
        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(yesterdayCheckIn);
        when(checkInRepository.findByDate(TODAY)).thenReturn(null);

        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(TODAY, savedCheckIn.getDate());
        assertEquals(5, savedCheckIn.getCount());
        assertEquals(4, savedCheckIn.getStreak());
    }

    @Test
    void testCheckInToday_whenTodayHasCommitsNoCheckInAndYesterdayNoCheckIn_shouldCreateNewCheckInResetStreak() {
        // Given
        Project project = new Project("shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", LocalDate.now())).thenReturn(0);

        CheckIn yesterdayCheckIn = new CheckIn(null, YESTERDAY, 2, 3);
        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(yesterdayCheckIn);
        when(checkInRepository.findByDate(TODAY)).thenReturn(null);

        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(TODAY, savedCheckIn.getDate());
        assertEquals(0, savedCheckIn.getCount());
        assertEquals(0, savedCheckIn.getStreak());
    }

    @Test
    void testCheckInToday_whenTodayNoCommitsNoCheckInAndYesterdayNoCheckIn_shouldCreateNewCheckInZeroStreak() {
        // Given
        Project project = new Project("shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", LocalDate.now())).thenReturn(0);
        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(null);
        when(checkInRepository.findByDate(TODAY)).thenReturn(null);
        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(TODAY, savedCheckIn.getDate());
        assertEquals(0, savedCheckIn.getCount());
        assertEquals(0, savedCheckIn.getStreak());
    }

    @Test
    void testCheckInToday_whenTodayHasCommitsHasCheckInAndYesterdayHasCheckIn_shouldUpdateExistingCheckInAndContinueStreak() {
        // Given
        Project project = new Project("shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", LocalDate.now())).thenReturn(7);

        CheckIn yesterdayCheckIn = new CheckIn(1L, YESTERDAY, 3, 1);
        CheckIn existingCheckIn = new CheckIn(2L, TODAY, 3, 2);
        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(yesterdayCheckIn);
        when(checkInRepository.findByDate(TODAY)).thenReturn(existingCheckIn);

        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(existingCheckIn.getId(), savedCheckIn.getId());
        assertEquals(TODAY, savedCheckIn.getDate());
        assertEquals(7, savedCheckIn.getCount());
        assertEquals(2, savedCheckIn.getStreak());
    }

    @Test
    void testCheckInToday_whenMultipleProjectsExistWithCommits_shouldCreateNewCheckInWithSumAllCommitCounts() {
        // Given
        Project project1 = new Project("shazxrin/project1");
        Project project2 = new Project("shazxrin/project2");
        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));

        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "project1", LocalDate.now())).thenReturn(3);
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "project2", LocalDate.now())).thenReturn(4);

        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(null);
        when(checkInRepository.findByDate(TODAY)).thenReturn(null);

        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(TODAY, savedCheckIn.getDate());
        assertEquals(7, savedCheckIn.getCount());
        assertEquals(1, savedCheckIn.getStreak());
    }

    @Test
    void testCheckInToday_whenNoProjectsExist_shouldCreateNewCheckInWithZeroCommitCount() {
        // Given
        when(projectService.getAllProjects()).thenReturn(Collections.emptyList());
        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(null);
        when(checkInRepository.findByDate(TODAY)).thenReturn(null);
        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        checkInService.checkInToday();

        // Then
        verify(checkInRepository).save(checkInCaptor.capture());
        CheckIn savedCheckIn = checkInCaptor.getValue();
        assertEquals(TODAY, savedCheckIn.getDate());
        assertEquals(0, savedCheckIn.getCount());
        assertEquals(0, savedCheckIn.getStreak());
    }

    @Test
    void testGetTodaysCheckIn_whenTodaysCheckInExists_shouldReturnExistingCheckIn() {
        // Given
        CheckIn existingCheckIn = new CheckIn(1L, TODAY, 5, 3);
        when(checkInRepository.findByDate(TODAY)).thenReturn(existingCheckIn);

        // When
        CheckIn todaysCheckIn = checkInService.getTodaysCheckIn();
    
        // Then
        // Verify no new check-in was saved
        verify(checkInRepository, never()).save(any());

        assertEquals(existingCheckIn, todaysCheckIn);
        assertEquals(1L, todaysCheckIn.getId());
        assertEquals(TODAY, todaysCheckIn.getDate());
        assertEquals(5, todaysCheckIn.getCount());
        assertEquals(3, todaysCheckIn.getStreak());
    }
    
    @Test
    void testGetTodaysCheckIn_whenTodaysCheckInDoesNotExist_shouldCreateNewCheckInUsingCheckInToday() {
        // Given
        Project project = new Project("shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", LocalDate.now())).thenReturn(4);
        when(checkInRepository.findByDate(TODAY)).thenReturn(null);
        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(null);
        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CheckIn todaysCheckIn = checkInService.getTodaysCheckIn();
    
        // Then
        // Verify a new check-in was saved
        verify(checkInRepository).save(any());

        assertEquals(TODAY, todaysCheckIn.getDate());
        assertEquals(4, todaysCheckIn.getCount());
        assertEquals(1, todaysCheckIn.getStreak());
    }
    
    @Test
    void testGetTodaysCheckIn_whenTodaysCheckInDoesNotExistAndNoCommits_shouldReturnNewCheckInWithZeroStreak() {
        // Given
        when(projectService.getAllProjects()).thenReturn(Collections.emptyList());
        when(checkInRepository.findByDate(TODAY)).thenReturn(null);
        when(checkInRepository.findByDate(YESTERDAY)).thenReturn(null);
        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CheckIn todaysCheckIn = checkInService.getTodaysCheckIn();
    
        // Then
        // Verify a new check-in was saved
        verify(checkInRepository).save(any());
        assertEquals(TODAY, todaysCheckIn.getDate());
        assertEquals(0, todaysCheckIn.getCount());
        assertEquals(0, todaysCheckIn.getStreak());
    }
    
    @Test
    void testCheckInInterval_whenBootstrapDateIsInFuture_shouldThrowException() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        // When/Then
        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> checkInService.checkInInterval(futureDate, TODAY)
        );
        
        assertEquals("From date must be before to date.", exception.getMessage());
        verify(checkInRepository, never()).save(any());
    }
    
    @Test
    void testCheckInInterval_whenBootstrapDateIsYesterday_shouldCreateCheckInsForYesterdayAndToday() {
        // Given
        Project project = new Project(null, "shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));

        LocalDate twoDaysAgo = TODAY.minusDays(2);

        // Configure git commit counts
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", YESTERDAY)).thenReturn(3);
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", TODAY)).thenReturn(5);
        
        // No existing check-ins
        when(checkInRepository.findByDate(twoDaysAgo))
            .thenReturn(null);
        when(checkInRepository.findByDate(YESTERDAY))
            .thenReturn(null)
            .thenReturn(new CheckIn(1L, YESTERDAY, 3, 1));
        when(checkInRepository.findByDate(TODAY))
            .thenReturn(null);

        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        checkInService.checkInInterval(YESTERDAY, TODAY);
        
        // Then
        ArgumentCaptor<CheckIn> checkInCaptor = ArgumentCaptor.forClass(CheckIn.class);
        verify(checkInRepository, times(2)).save(checkInCaptor.capture());
        
        List<CheckIn> savedCheckIns = checkInCaptor.getAllValues();
        assertEquals(2, savedCheckIns.size());
        
        // Verify yesterday's check-in
        CheckIn yesterdayCheckIn = savedCheckIns.get(0);
        assertEquals(YESTERDAY, yesterdayCheckIn.getDate());
        assertEquals(3, yesterdayCheckIn.getCount());
        assertEquals(1, yesterdayCheckIn.getStreak()); // First day with commits
        
        // Verify today's check-in
        CheckIn todayCheckIn = savedCheckIns.get(1);
        assertEquals(TODAY, todayCheckIn.getDate());
        assertEquals(5, todayCheckIn.getCount());
        assertEquals(2, todayCheckIn.getStreak()); // Second day with commits
    }
    
    @Test
    void testCheckInInterval_whenBootstrapDateIsThreeDaysAgo_shouldCreateCheckInsForAllDays() {
        // Given
        Project project = new Project("shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));

        LocalDate fourDaysAgo = TODAY.minusDays(4);
        LocalDate threeDaysAgo = TODAY.minusDays(3);
        LocalDate twoDaysAgo = TODAY.minusDays(2);
        
        // Configure git commit counts
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", threeDaysAgo)).thenReturn(2);
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", twoDaysAgo)).thenReturn(0); // No commits
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", YESTERDAY)).thenReturn(3);
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", TODAY)).thenReturn(4);
        
        // No existing check-ins
        when(checkInRepository.findByDate(fourDaysAgo))
            .thenReturn(null);
        when(checkInRepository.findByDate(threeDaysAgo))
            .thenReturn(null)
            .thenReturn(new CheckIn(1L, threeDaysAgo, 2, 1));
        when(checkInRepository.findByDate(twoDaysAgo))
            .thenReturn(null)
            .thenReturn(new CheckIn(2L, twoDaysAgo, 0, 0));
        when(checkInRepository.findByDate(YESTERDAY))
            .thenReturn(null)
            .thenReturn(new CheckIn(3L, YESTERDAY, 3, 1));
        when(checkInRepository.findByDate(TODAY))
            .thenReturn(null);
        
        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        checkInService.checkInInterval(threeDaysAgo, TODAY);
        
        // Then
        ArgumentCaptor<CheckIn> checkInCaptor = ArgumentCaptor.forClass(CheckIn.class);
        verify(checkInRepository, times(4)).save(checkInCaptor.capture());
        
        List<CheckIn> savedCheckIns = checkInCaptor.getAllValues();
        assertEquals(4, savedCheckIns.size());
        
        // Verify three days ago check-in
        CheckIn threeDaysAgoCheckIn = savedCheckIns.get(0);
        assertEquals(threeDaysAgo, threeDaysAgoCheckIn.getDate());
        assertEquals(2, threeDaysAgoCheckIn.getCount());
        assertEquals(1, threeDaysAgoCheckIn.getStreak());
        
        // Verify two days ago check-in (streak broken)
        CheckIn twoDaysAgoCheckIn = savedCheckIns.get(1);
        assertEquals(twoDaysAgo, twoDaysAgoCheckIn.getDate());
        assertEquals(0, twoDaysAgoCheckIn.getCount());
        assertEquals(0, twoDaysAgoCheckIn.getStreak());
        
        // Verify yesterday's check-in (new streak started)
        CheckIn yesterdayCheckIn = savedCheckIns.get(2);
        assertEquals(YESTERDAY, yesterdayCheckIn.getDate());
        assertEquals(3, yesterdayCheckIn.getCount());
        assertEquals(1, yesterdayCheckIn.getStreak());
        
        // Verify today's check-in
        CheckIn todayCheckIn = savedCheckIns.get(3);
        assertEquals(TODAY, todayCheckIn.getDate());
        assertEquals(4, todayCheckIn.getCount());
        assertEquals(2, todayCheckIn.getStreak());
    }
    
    @Test
    void testCheckInInterval_whenSomeCheckInsAlreadyExist_shouldUpdateExistingAndCreateMissing() {
        // Given
        Project project = new Project("shazxrin/one-percent-better");
        when(projectService.getAllProjects()).thenReturn(List.of(project));

        LocalDate threeDaysAgo = TODAY.minusDays(3);
        LocalDate twoDaysAgo = TODAY.minusDays(2);
        
        // Configure git commit counts - all days have commits now
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", twoDaysAgo)).thenReturn(2);
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", YESTERDAY)).thenReturn(3);
        when(gitHubService.getCommitCountForRepositoryOnDate("shazxrin", "one-percent-better", TODAY)).thenReturn(5);
        
        // Yesterday already has a check-in with outdated data
        CheckIn existingYesterdayCheckIn = new CheckIn(1L, YESTERDAY, 1, 1);
        when(checkInRepository.findByDate(threeDaysAgo))
            .thenReturn(null);
        when(checkInRepository.findByDate(twoDaysAgo))
            .thenReturn(null)
            .thenReturn(new CheckIn(2L, twoDaysAgo, 2, 1));
        when(checkInRepository.findByDate(YESTERDAY))
            .thenReturn(existingYesterdayCheckIn)
            .thenReturn(new CheckIn(3L, YESTERDAY, 3, 2));
        when(checkInRepository.findByDate(TODAY))
            .thenReturn(null);
        
        when(checkInRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        checkInService.checkInInterval(twoDaysAgo, TODAY);
        
        // Then
        ArgumentCaptor<CheckIn> checkInCaptor = ArgumentCaptor.forClass(CheckIn.class);
        verify(checkInRepository, times(3)).save(checkInCaptor.capture());
        
        List<CheckIn> savedCheckIns = checkInCaptor.getAllValues();
        assertEquals(3, savedCheckIns.size());
        
        // Verify two days ago check-in
        CheckIn twoDaysAgoCheckIn = savedCheckIns.get(0);
        assertEquals(twoDaysAgo, twoDaysAgoCheckIn.getDate());
        assertEquals(2, twoDaysAgoCheckIn.getCount());
        assertEquals(1, twoDaysAgoCheckIn.getStreak());
        
        // Verify yesterday's check-in was updated
        CheckIn yesterdayCheckIn = savedCheckIns.get(1);
        assertEquals(YESTERDAY, yesterdayCheckIn.getDate());
        assertEquals(3, yesterdayCheckIn.getCount()); // Updated count
        assertEquals(2, yesterdayCheckIn.getStreak()); // Updated streak
        
        // Verify today's check-in
        CheckIn todayCheckIn = savedCheckIns.get(2);
        assertEquals(TODAY, todayCheckIn.getDate());
        assertEquals(5, todayCheckIn.getCount());
        assertEquals(3, todayCheckIn.getStreak());
    }
}
