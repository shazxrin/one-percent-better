package io.github.shazxrin.onepercentbetter.checkin.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProjectDailySummary;
import io.github.shazxrin.onepercentbetter.checkin.repository.CheckInProjectDailySummaryRepository;
import io.github.shazxrin.onepercentbetter.checkin.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void testInitSummaries_shouldCreateSummariesForAllProjects() {
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
    public void testGetSummary_whenSummaryExists_shouldReturnExistingSummary() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary summary = new CheckInProjectDailySummary(date, 2, 3, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(java.util.Optional.of(summary));

        CheckInProjectDailySummary result = checkInProjectDailySummaryService.getSummary(projectId, date);

        assertEquals(summary, result);
        verify(checkInProjectDailySummaryRepository, times(1)).findByProjectIdAndDate(projectId, date);
        verify(checkInProjectDailySummaryRepository, times(0)).save(any());
    }

    @Test
    public void testGetSummary_whenSummaryDoesNotExist_shouldCreateNewSummaryAndSave() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary savedSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(java.util.Optional.empty());
        when(checkInProjectDailySummaryRepository.save(any(CheckInProjectDailySummary.class))).thenReturn(savedSummary);

        CheckInProjectDailySummary result = checkInProjectDailySummaryService.getSummary(projectId, date);

        assertNotNull(result);
        assertEquals(savedSummary, result);
        assertEquals(0, result.getNoOfCheckIns());
        assertEquals(0, result.getStreak());
        assertEquals(project, result.getProject());
        verify(checkInProjectDailySummaryRepository, times(1)).save(any(CheckInProjectDailySummary.class));
    }

    @Test
    public void testGetSummary_whenProjectDoesNotExist_shouldThrowException() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        when(projectService.getProjectById(projectId)).thenThrow(new RuntimeException("Project not found"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            checkInProjectDailySummaryService.getSummary(projectId, date);
        });
    }

    @Test
    public void testCalculateSummary_whenCheckInExistsshouldUpdateSummaryWithCheckInCounts() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        LocalDate previousDate = date.minusDays(1);
        Project project = new Project(projectId, "Project 1");
        CheckInProjectDailySummary previousSummary = new CheckInProjectDailySummary(previousDate, 1, 2, project);
        CheckInProjectDailySummary currentSummary = new CheckInProjectDailySummary(date, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, previousDate)).thenReturn(java.util.Optional.of(previousSummary));
        when(checkInProjectDailySummaryRepository.findByProjectIdAndDate(projectId, date)).thenReturn(java.util.Optional.of(currentSummary));
        when(checkInProjectRepository.countByDate(date)).thenReturn(3);

        checkInProjectDailySummaryService.calculateSummary(projectId, date);

        ArgumentCaptor<CheckInProjectDailySummary> captor = ArgumentCaptor.forClass(CheckInProjectDailySummary.class);
        verify(checkInProjectDailySummaryRepository).save(captor.capture());
        CheckInProjectDailySummary savedSummary = captor.getValue();
        assertEquals(3, savedSummary.getNoOfCheckIns());
        assertEquals(previousSummary.getStreak() + 1, savedSummary.getStreak());
        verify(applicationEventPublisher, times(1)).publishEvent(any());
    }
}