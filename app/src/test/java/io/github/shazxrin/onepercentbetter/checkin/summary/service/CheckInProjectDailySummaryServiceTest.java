package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectDailySummaryServiceTest {
    @Mock
    private CheckInProjectService checkInProjectService;

    @Mock
    private CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    @Test
    void testGetSummary_whenSummaryExists_shouldReturnSummary() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary summary = new CheckInProjectDailySummary(date, 2, 3, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(summary));

        CheckInProjectDailySummary result = checkInProjectDailySummaryService.getSummary(projectId, date);

        assertEquals(summary, result);
        verify(checkInProjectDailySummaryRepository, times(1)).findByProjectIdAndDate(projectId, date);
        verify(checkInProjectDailySummaryRepository, times(0)).save(any());
    }

    @Test
    void testGetSummary_whenSummaryDoesNotExist_shouldThrowException() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        Project project = new Project("Project 1");
        project.setId(projectId);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> checkInProjectDailySummaryService.getSummary(projectId, date));
    }

    @Test
    void testGetSummary_whenProjectDoesNotExist_shouldThrowException() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        when(projectService.getProjectById(projectId)).thenThrow(new RuntimeException("Project not found"));

        assertThrows(RuntimeException.class, () -> {
            checkInProjectDailySummaryService.getSummary(projectId, date);
        });
    }

    @Test
    void testCalculateSummaryForDate_whenPreviousHasStreakAndCurrentHasCheckIns_shouldContinueStreak() {
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDate previousDate = date.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 2, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.of(currentSummary));

        CheckInProject checkInProject1 = new CheckInProject(dateTime, "a1", "feat", "message", project);
        CheckInProject checkInProject2 = new CheckInProject(dateTime, "a2", "feat", "message", project);
        CheckInProject checkInProject3 = new CheckInProject(dateTime, "a3", "feat", "message", project);
        when(checkInProjectService.getAllCheckInsByProject(projectId, date)).thenReturn(List.of(checkInProject1, checkInProject2, checkInProject3));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(previousSummary.getStreak() + 1, savedSummary.getStreak());
        assertEquals(3, savedSummary.getTypeDistribution().get("feat"));
        assertEquals(3, savedSummary.getHourDistribution().get(String.valueOf(dateTime.getHour())));
    }
    
    @Test
    void testCalculateSummaryForDate_whenPreviousHasStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 3, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.of(currentSummary));

        when(checkInProjectService.getAllCheckInsByProject(projectId, date)).thenReturn(List.of());

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
        assertNull(savedSummary.getTypeDistribution().get("feat"));
    }
    
    @Test
    void testCalculateSummaryForDate_whenPreviousHasNoStreakAndCurrentHasCheckIns_shouldStartStreakForCurrent() {
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = date.atTime(LocalTime.MIN);
        LocalDate previousDate = date.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.of(currentSummary));

        CheckInProject checkInProject1 = new CheckInProject(dateTime, "a1", "feat", "message", project);
        CheckInProject checkInProject2 = new CheckInProject(dateTime, "a2", "feat", "message", project);
        when(checkInProjectService.getAllCheckInsByProject(projectId, date)).thenReturn(List.of(checkInProject1, checkInProject2));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
        assertEquals(2, savedSummary.getTypeDistribution().get("feat"));
        assertEquals(2, savedSummary.getHourDistribution().get(String.valueOf(dateTime.getHour())));
    }
    
    @Test
    void testCalculateSummaryForDate_whenPreviousHasNoStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.of(currentSummary));

        when(checkInProjectService.getAllCheckInsByProject(projectId, date)).thenReturn(List.of());

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
        assertNull(savedSummary.getTypeDistribution().get("feat"));
    }
    
    @Test
    void testCalculateSummaryForDate_whenWithoutCountAndPreviousHasStreakAndCurrentHasCheckIns_shouldContinueStreak() {
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 2, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 3, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.of(currentSummary));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, false);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(previousSummary.getStreak() + 1, savedSummary.getStreak());
    }

    @Test
    void testCalculateSummaryForDate_whenWithoutCountAndPreviousHasStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 3, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.of(currentSummary));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, false);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }

    @Test
    void testCalculateSummaryForDate_whenWithoutCountPreviousHasNoStreakAndCurrentHasCheckIns_shouldStartStreakForCurrent() {
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 2, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.of(currentSummary));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, false);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
    }

    @Test
    void testCalculateSummaryForDate_whenWithoutCountPreviousHasNoStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.of(currentSummary));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, false);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }

    @Test
    void testCalculateSummaryForDate_whenPreviousDoesNotExist_shouldThrowException() {
        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.empty());

        assertThrows(
            IllegalStateException.class,
            () -> checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true)
        );
    }

    @Test
    void testCalculateSummaryForDate_whenCurrentDoesNotExist_shouldThrowException() {
        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        LocalDate date = LocalDate.now();
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.empty());

        LocalDate previousDate = date.minusDays(1);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));

        assertThrows(
            IllegalStateException.class,
            () -> checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true)
        );
    }

    @Test
    void testCalculateSummaryForDate_whenProjectDoesNotExist_shouldThrowException() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> checkInProjectDailySummaryService.calculateSummaryForDate(projectId, LocalDate.now(), true));
    }

    @Test
    void testAddCheckInToSummary_whenPreviousHasStreakAndDateIsToday_shouldIncrementNoOfCheckInsAndStreakForToday() {
        long projectId = 1L;
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDate yesterday = today.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);

        long checkInProjectId = 1L;
        CheckInProject checkInProject = new CheckInProject(now, "abc123", "feat", "message", project);
        checkInProject.setId(checkInProjectId);
        when(checkInProjectService.getCheckIn(checkInProjectId)).thenReturn(Optional.of(checkInProject));
        
        CheckInProjectDailySummary yesterdaySummary = new CheckInProjectDailySummary(yesterday, 1, 3, project);
        CheckInProjectDailySummary todaySummary = new CheckInProjectDailySummary(today, 1, 0, project);
        
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, yesterday)).thenReturn(Optional.of(yesterdaySummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, today)).thenReturn(Optional.of(todaySummary));
        
        checkInProjectDailySummaryService.addCheckInToSummary(projectId, checkInProjectId);
        
        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(4, savedSummary.getStreak());
        assertEquals(1, savedSummary.getTypeDistribution().get("feat"));
        assertEquals(1, savedSummary.getHourDistribution().get(String.valueOf(now.getHour())));
    }

    @Test
    void testAddCheckInToSummary_whenPreviousHasNoStreakAndDateIsToday_shouldIncrementNoOfCheckInsAndStartStreakForToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDate yesterday = today.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        long checkInProjectId = 1L;
        CheckInProject checkInProject = new CheckInProject(now, "abc123", "feat", "message", project);
        checkInProject.setId(checkInProjectId);
        when(checkInProjectService.getCheckIn(checkInProjectId)).thenReturn(Optional.of(checkInProject));

        CheckInProjectDailySummary yesterdaySummary = new CheckInProjectDailySummary(yesterday, 0, 0, project);
        CheckInProjectDailySummary todaySummary = new CheckInProjectDailySummary(today, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, yesterday)).thenReturn(Optional.of(yesterdaySummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, today)).thenReturn(Optional.of(todaySummary));
        
        checkInProjectDailySummaryService.addCheckInToSummary(projectId, checkInProjectId);
        
        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(1, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
        assertEquals(1, savedSummary.getTypeDistribution().get("feat"));
        assertEquals(1, savedSummary.getHourDistribution().get(String.valueOf(now.getHour())));
    }

    @Test
    void testAddCheckInToSummary_whenDateIsFewDaysBeforeToday_shouldIncrementNoOfCheckInsAndCalculateStreakForFollowingDays() {
        LocalDate today = LocalDate.now();
        LocalDate fourDaysAgo = today.minusDays(4);
        LocalDate threeDaysAgo = today.minusDays(3);
        LocalDateTime threeDaysAgoWithTime = LocalDateTime.now().minusDays(3);
        LocalDate twoDaysAgo = today.minusDays(2);
        LocalDate yesterday = today.minusDays(1);

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        long checkInProjectId = 1L;
        CheckInProject checkInProject = new CheckInProject(threeDaysAgoWithTime, "abc123", "feat", "message", project);
        checkInProject.setId(checkInProjectId);
        when(checkInProjectService.getCheckIn(checkInProjectId)).thenReturn(Optional.of(checkInProject));

        CheckInProjectDailySummary fourDaysAgoSummary = new CheckInProjectDailySummary(threeDaysAgo, 0, 0, project);
        CheckInProjectDailySummary threeDaysAgoSummary = new CheckInProjectDailySummary(threeDaysAgo, 0, 0, project);
        CheckInProjectDailySummary updatedThreeDaysAgoSummary = new CheckInProjectDailySummary(threeDaysAgo, 1, 1, project);
        CheckInProjectDailySummary twoDaysAgoSummary = new CheckInProjectDailySummary(twoDaysAgo, 1, 1, project);
        CheckInProjectDailySummary yesterdaySummary = new CheckInProjectDailySummary(yesterday, 1, 2, project);
        CheckInProjectDailySummary todaySummary = new CheckInProjectDailySummary(today, 1, 3, project);

        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, fourDaysAgo)).thenReturn(Optional.of(fourDaysAgoSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, threeDaysAgo)).thenReturn(Optional.of(threeDaysAgoSummary));

        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, threeDaysAgo)).thenReturn(Optional.of(updatedThreeDaysAgoSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, twoDaysAgo)).thenReturn(Optional.of(twoDaysAgoSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, twoDaysAgo)).thenReturn(Optional.of(twoDaysAgoSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, yesterday)).thenReturn(Optional.of(yesterdaySummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, yesterday)).thenReturn(Optional.of(yesterdaySummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, today)).thenReturn(Optional.of(todaySummary));

        checkInProjectDailySummaryService.addCheckInToSummary(projectId, checkInProjectId);
        
        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository, times(4)).save(captor.capture());

        List<CheckInProjectDailySummary> savedSummaries = captor.getAllValues();
        CheckInProjectDailySummary updatedThreeDaysAgo = savedSummaries.get(0);
        assertEquals(1, updatedThreeDaysAgo.getNoOfCheckIns());
        assertEquals(1, updatedThreeDaysAgo.getStreak());
        assertEquals(1, updatedThreeDaysAgo.getTypeDistribution().get("feat"));
        assertEquals(1, updatedThreeDaysAgo.getHourDistribution().get(String.valueOf(threeDaysAgoWithTime.getHour())));

        CheckInProjectDailySummary updatedTwoDaysAgo = savedSummaries.get(1);
        assertEquals(1, updatedTwoDaysAgo.getNoOfCheckIns());
        assertEquals(2, updatedTwoDaysAgo.getStreak());

        CheckInProjectDailySummary updatedYesterday = savedSummaries.get(2);
        assertEquals(1, updatedYesterday.getNoOfCheckIns());
        assertEquals(3, updatedYesterday.getStreak());

        CheckInProjectDailySummary updatedToday = savedSummaries.get(3);
        assertEquals(1, updatedToday.getNoOfCheckIns());
        assertEquals(4, updatedToday.getStreak());
    }

    @Test
    void testAddCheckInToSummary_whenProjectDoesNotExist_shouldThrowException() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

        long checkInProjectId = 1L;

        assertThrows(ProjectNotFoundException.class, () -> checkInProjectDailySummaryService.addCheckInToSummary(projectId, checkInProjectId));
    }

    @Test
    void testAddCheckInToSummary_whenPreviousSummaryDoesNotExist_shouldThrowException() {
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        long checkInProjectId = 1L;
        CheckInProject checkInProject = new CheckInProject(dateTime, "abc123", "feat", "message", project);
        checkInProject.setId(checkInProjectId);
        when(checkInProjectService.getCheckIn(checkInProjectId)).thenReturn(Optional.of(checkInProject));

        LocalDate previousDate = date.minusDays(1);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> checkInProjectDailySummaryService.addCheckInToSummary(projectId, checkInProjectId));
    }

    @Test
    void testAddCheckInToSummary_whenCurrentSummaryDoesNotExist_shouldThrowException() {
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();

        long projectId = 1L;
        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        long checkInProjectId = 1L;
        CheckInProject checkInProject = new CheckInProject(dateTime, "abc123", "feat", "message", project);
        checkInProject.setId(checkInProjectId);
        when(checkInProjectService.getCheckIn(checkInProjectId)).thenReturn(Optional.of(checkInProject));

        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, date)).thenReturn(Optional.empty());

        LocalDate previousDate = date.minusDays(1);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));

        assertThrows(IllegalStateException.class, () -> checkInProjectDailySummaryService.addCheckInToSummary(projectId, checkInProjectId));
    }

    @Test
    void testInitSummaries_shouldCreateSummariesForAllProjects() {
        Project project1 = new Project("Project 1");
        project1.setId(1L);
        Project project2 = new Project("Project 2");
        project2.setId(2L);
        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(project1));
        when(projectService.getProjectById(2L)).thenReturn(Optional.of(project2));

        checkInProjectDailySummaryService.initSummaries();

        ArgumentCaptor<List<CheckInProjectDailySummary>> captor = ArgumentCaptor.forClass(List.class);
        verify(checkInProjectDailySummaryRepository, times(2)).saveAll(captor.capture());

        List<List<CheckInProjectDailySummary>> allSavedSummaries = captor.getAllValues();
        assertEquals(2, allSavedSummaries.size());

        // Verify project1 summaries
        List<CheckInProjectDailySummary> project1Summaries = allSavedSummaries.get(0);
        assertEquals(LocalDate.now().lengthOfYear(), project1Summaries.size());

        // Verify project2 summaries
        List<CheckInProjectDailySummary> project2Summaries = allSavedSummaries.get(1);
        assertEquals(LocalDate.now().lengthOfYear(), project2Summaries.size());
    }
}