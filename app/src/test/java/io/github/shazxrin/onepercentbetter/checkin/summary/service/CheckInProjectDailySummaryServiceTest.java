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
import org.springframework.context.ApplicationEventPublisher;

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

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    @Test
    void testInitSummaries_shouldCreateSummariesForAllProjects() {
        LocalDate date = LocalDate.now();
        Project project1 = new Project(1L, "Project 1");
        Project project2 = new Project(2L, "Project 2");
        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));

        checkInProjectDailySummaryService.initSummaries(date);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository, times(2)).save(captor.capture());
        List<CheckInProjectDailySummary> savedSummaries = captor.getAllValues();
        assertEquals(2, savedSummaries.size());
        assertEquals(project1, savedSummaries.get(0).getProject());
        assertEquals(date, savedSummaries.get(0).getDate());
        assertEquals(0, savedSummaries.get(0).getNoOfCheckIns());
        assertEquals(0, savedSummaries.get(0).getStreak());
        assertEquals(project2, savedSummaries.get(1).getProject());
    }

    @Test
    void testGetSummary_whenSummaryExists_shouldReturnExistingSummary() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary summary = new CheckInProjectDailySummary(date, 2, 3, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
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
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary savedSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
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
    void testCalculateSummary_whenPreviousHasStreakAndCurrentHasCheckIns_shouldContinueStreak() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 2, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByDate(date)).thenReturn(3);

        checkInProjectDailySummaryService.calculateSummary(projectId, date);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(previousSummary.getStreak() + 1, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummary_whenPreviousHasStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 3, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByDate(date)).thenReturn(0);

        checkInProjectDailySummaryService.calculateSummary(projectId, date);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummary_whenPreviousHasNoStreakAndCurrentHasCheckIns_shouldStartStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByDate(date)).thenReturn(2);

        checkInProjectDailySummaryService.calculateSummary(projectId, date);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummary_whenPreviousHasNoStreakAndCurrentHasNoCheckIns_shouldHaveZeroStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 0, 0, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByDate(date)).thenReturn(0);

        checkInProjectDailySummaryService.calculateSummary(projectId, date);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummary_whenPreviousIsMissingAndCurrentHaveCheckIns_shouldStartStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.empty());
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByDate(date)).thenReturn(2);

        checkInProjectDailySummaryService.calculateSummary(projectId, date);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(2, savedSummary.getNoOfCheckIns());
        assertEquals(1, savedSummary.getStreak());
    }
    
    @Test
    void testCalculateSummary_whenPreviousIsMissingAndCurrentHaveNoCheckIns_shouldHaveZeroStreakForCurrent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(Optional.empty());
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(Optional.of(currentSummary));
        when(checkInProjectRepository.countByDate(date)).thenReturn(0);

        checkInProjectDailySummaryService.calculateSummary(projectId, date);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(0, savedSummary.getNoOfCheckIns());
        assertEquals(0, savedSummary.getStreak());
    }
}