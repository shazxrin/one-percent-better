package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectDailySummaryServiceTest {
    @Mock
    private CheckInProjectDailySummaryRepository checkInProjectDailySummaryRepository;

    @Mock
    private CheckInProjectRepository checkInProjectRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private CheckInProjectDailySummaryService checkInProjectDailySummaryService;

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

    @Test
    void testGetSummary_whenSummaryExists_shouldReturnExistingSummary() {
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
    void testGetSummary_whenSummaryDoesNotExist_shouldCreateNewSummaryAndSave() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary savedSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.empty());
        when(checkInProjectDailySummaryRepository.save(any())).thenReturn(savedSummary);

        CheckInProjectDailySummary result = checkInProjectDailySummaryService.getSummary(projectId, date);

        assertNotNull(result);
        assertEquals(savedSummary, result);
        assertEquals(0, result.getNoOfCheckIns());
        assertEquals(0, result.getStreak());
        assertEquals(project, result.getProject());
        verify(checkInProjectDailySummaryRepository, times(1)).save(any());
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
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 2, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByProjectIdAndDate(projectId, date)).thenReturn(3);

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(previousSummary.getStreak() + 1, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummaryForDate_whenPreviousHasStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 3, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByProjectIdAndDate(projectId, date)).thenReturn(0);

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummaryForDate_whenPreviousHasNoStreakAndCurrentHasCheckIns_shouldStartStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByProjectIdAndDate(projectId, date)).thenReturn(2);

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummaryForDate_whenPreviousHasNoStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByProjectIdAndDate(projectId, date)).thenReturn(0);

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummaryForDate_whenWithoutCountAndPreviousHasStreakAndCurrentHasCheckIns_shouldContinueStreak() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 2, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 3, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, false);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(3, savedSummary.getNoOfCheckIns()); // Should keep existing count
        assertEquals(previousSummary.getStreak() + 1, savedSummary.getStreak());
    }

    @Test
    void testCalculateSummaryForDate_whenWithoutCountAndPreviousHasStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 3, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, false);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }

    @Test
    void testCalculateSummaryForDate_whenWithoutCountPreviousHasNoStreakAndCurrentHasCheckIns_shouldStartStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 2, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, false);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
    }

    @Test
    void testCalculateSummaryForDate_whenWithoutCountPreviousHasNoStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));

        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, false);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }

    @Test
    void testAddCheckInToSummary_whenPreviousHasStreakAndDateIsToday_shouldIncrementNoOfCheckInsAndStreakForToday() {
        long projectId = 1L;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        
        CheckInProjectDailySummary yesterdaySummary = new CheckInProjectDailySummary(yesterday, 1, 3, project);
        CheckInProjectDailySummary todaySummary = new CheckInProjectDailySummary(today, 1, 0, project);
        
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, yesterday)).thenReturn(Optional.of(yesterdaySummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, today)).thenReturn(Optional.of(todaySummary));
        
        checkInProjectDailySummaryService.addCheckInToSummary(projectId, today);
        
        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(4, savedSummary.getStreak());
    }

    @Test
    void testAddCheckInToSummary_whenPreviousHasNoStreakAndDateIsToday_shouldIncrementNoOfCheckInsAndStartStreakForToday() {
        long projectId = 1L;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        Project project = new Project("Project 1");
        project.setId(projectId);
        
        CheckInProjectDailySummary yesterdaySummary = new CheckInProjectDailySummary(yesterday, 0, 0, project);
        CheckInProjectDailySummary todaySummary = new CheckInProjectDailySummary(today, 0, 0, project);
        
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, yesterday)).thenReturn(Optional.of(yesterdaySummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, today)).thenReturn(Optional.of(todaySummary));
        
        checkInProjectDailySummaryService.addCheckInToSummary(projectId, today);
        
        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(1, savedSummary.getNoOfCheckIns()); // Incremented from 0 to 1
        assertEquals(1, savedSummary.getStreak()); // Started new streak at 1
    }

    @Test
    void testAddCheckInToSummary_whenDateIsFewDaysBeforeToday_shouldIncrementNoOfCheckInsAndCalculateStreakForFollowingDays() {
        long projectId = 1L;
        LocalDate today = LocalDate.now();
        LocalDate threeDaysAgo = today.minusDays(3);
        LocalDate twoDaysAgo = today.minusDays(2);
        LocalDate yesterday = today.minusDays(1);

        Project project = new Project("Project 1");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        CheckInProjectDailySummary threeDaysAgoSummary = new CheckInProjectDailySummary(threeDaysAgo, 0, 0, project);
        CheckInProjectDailySummary updatedThreeDaysAgoSummary = new CheckInProjectDailySummary(threeDaysAgo, 1, 1, project);
        CheckInProjectDailySummary twoDaysAgoSummary = new CheckInProjectDailySummary(twoDaysAgo, 1, 1, project);
        CheckInProjectDailySummary yesterdaySummary = new CheckInProjectDailySummary(yesterday, 1, 2, project);
        CheckInProjectDailySummary todaySummary = new CheckInProjectDailySummary(today, 1, 3, project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, threeDaysAgo.minusDays(1))).thenReturn(Optional.empty());
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDateWithLock(projectId, threeDaysAgo)).thenReturn(Optional.of(threeDaysAgoSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, threeDaysAgo)).thenReturn(Optional.of(updatedThreeDaysAgoSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, twoDaysAgo)).thenReturn(Optional.of(twoDaysAgoSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, yesterday)).thenReturn(Optional.of(yesterdaySummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, today)).thenReturn(Optional.of(todaySummary));

        checkInProjectDailySummaryService.addCheckInToSummary(projectId, threeDaysAgo);
        
        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository, times(4)).save(captor.capture());

        List<CheckInProjectDailySummary> savedSummaries = captor.getAllValues();
        CheckInProjectDailySummary updatedThreeDaysAgo = savedSummaries.get(0);
        assertEquals(1, updatedThreeDaysAgo.getNoOfCheckIns());
        assertEquals(1, updatedThreeDaysAgo.getStreak());

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
}