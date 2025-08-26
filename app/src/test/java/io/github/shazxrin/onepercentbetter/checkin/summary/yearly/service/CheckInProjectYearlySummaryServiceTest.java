package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.model.CheckInProjectYearlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.repository.CheckInProjectYearlySummaryRepository;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectYearlySummaryServiceTest {

    @Mock
    private CheckInProjectYearlySummaryRepository repository;

    @Mock
    private ProjectService projectService;

    @Mock
    private CheckInProjectService checkInProjectService;

    @InjectMocks
    private CheckInProjectYearlySummaryService checkInProjectYearlySummaryService;

    @Test
    void testGetSummary_whenSummaryExists_shouldReturnSummary() {
        long projectId = 1L;
        int year = 2025;
        Project project = new Project("repo");
        project.setId(projectId);
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        CheckInProjectYearlySummary summary = new CheckInProjectYearlySummary(year, start, end, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(repository.findByProjectIdAndYear(projectId, year)).thenReturn(Optional.of(summary));

        var result = checkInProjectYearlySummaryService.getSummary(projectId, year);
        assertEquals(summary, result);
    }

    @Test
    void testGetSummary_whenProjectDoesNotExist_shouldThrow() {
        when(projectService.getProjectById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> checkInProjectYearlySummaryService.getSummary(1L, 2025));
    }

    @Test
    void testGetSummary_whenSummaryDoesNotExist_shouldThrow() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(new Project("repo")));
        when(repository.findByProjectIdAndYear(anyLong(), anyInt())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> checkInProjectYearlySummaryService.getSummary(projectId, 2025));
    }

    @Test
    void testCalculateSummaryForYear_shouldComputeCountsDistributionsAndStreak() {
        long projectId = 1L;
        int year = 2025;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Project project = new Project("repo");
        project.setId(projectId);
        CheckInProjectYearlySummary summary = new CheckInProjectYearlySummary(year, start, end, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(repository.findByProjectIdAndYearWithLock(projectId, year)).thenReturn(Optional.of(summary));

        var ci1 = new CheckInProject(LocalDateTime.of(2025, 1, 1, 1, 0), "a", "feat", "msg", project);
        var ci2 = new CheckInProject(LocalDateTime.of(2025, 1, 2, 1, 30), "b", "bug", "msg", project);
        var ci3 = new CheckInProject(LocalDateTime.of(2025, 1, 3, 12, 0), "c", null, "msg", project);
        var ci4 = new CheckInProject(LocalDateTime.of(2025, 3, 15, 12, 30), "d", "feat", "msg", project);
        when(checkInProjectService.getAllCheckInsByProjectBetween(projectId, start, end)).thenReturn(List.of(ci1, ci2, ci3, ci4));

        checkInProjectYearlySummaryService.calculateSummaryForYear(projectId, year);

        assertEquals(4, summary.getNoOfCheckIns());
        assertEquals(3, summary.getStreak());
        assertEquals(2, summary.getTypeDistribution().get("feat"));
        assertEquals(1, summary.getTypeDistribution().get("bug"));
        assertEquals(1, summary.getTypeDistribution().get("unknown"));
        assertEquals(2, summary.getHourDistribution().get("12"));
        assertEquals(2, summary.getHourDistribution().get("1"));
        assertEquals(1, summary.getDayDistribution().get("1"));
        assertEquals(1, summary.getDayDistribution().get("2"));
        assertEquals(1, summary.getDayDistribution().get("3"));
        verify(repository, times(1)).save(summary);
    }

    @Test
    void testAddCheckInToSummary_whenNullType_shouldCountUnknownAndUpdateDistributionsAndStreak() {
        long projectId = 1L;
        Project project = new Project("repo");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        var checkIn = new CheckInProject(LocalDateTime.of(2025, 7, 31, 9, 0), "sha", null, "msg", project);
        when(checkInProjectService.getCheckIn(2L)).thenReturn(Optional.of(checkIn));

        int year = checkIn.getDateTime().getYear();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        CheckInProjectYearlySummary summary = new CheckInProjectYearlySummary(year, start, end, 0, 0, project);
        when(repository.findByProjectIdAndYearWithLock(projectId, year)).thenReturn(Optional.of(summary));

        checkInProjectYearlySummaryService.addCheckInToSummary(projectId, 2L);

        assertEquals(1, summary.getNoOfCheckIns());
        assertEquals(1, summary.getTypeDistribution().get("unknown"));
        assertEquals(1, summary.getHourDistribution().get("9"));
        assertEquals(1, summary.getDayDistribution().get("212"));
        assertEquals(1, summary.getStreak());
        verify(repository, times(1)).save(summary);
    }

    @Test
    void testAddCheckInToSummary_whenProjectDoesNotExist_shouldThrow() {
        when(projectService.getProjectById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> checkInProjectYearlySummaryService.addCheckInToSummary(1L, 2L));
    }

    @Test
    void testAddCheckInToSummary_whenCheckInDoesNotExist_shouldThrow() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(new Project("repo")));
        when(checkInProjectService.getCheckIn(anyLong())).thenReturn(Optional.empty());
        assertThrows(CheckInProjectNotFoundException.class, () -> checkInProjectYearlySummaryService.addCheckInToSummary(projectId, 2L));
    }

    @Test
    void testInitSummary_shouldCreateYearlySummaryForCurrentYearAndSave() {
        long projectId = 1L;
        Project project = new Project("repo");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        ArgumentCaptor<CheckInProjectYearlySummary> captor = ArgumentCaptor.forClass(CheckInProjectYearlySummary.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        checkInProjectYearlySummaryService.initSummary(projectId);

        verify(repository, times(1)).save(captor.capture());
        CheckInProjectYearlySummary summary = captor.getValue();
        assertNotNull(summary);
        assertEquals(LocalDate.now().getYear(), summary.getYear());
        assertEquals(Year.of(LocalDate.now().getYear()).atDay(1), summary.getStartDate());
        assertEquals(Year.of(LocalDate.now().getYear()).atDay(Year.of(LocalDate.now().getYear()).length()), summary.getEndDate());
    }

    @Test
    void testInitSummaries_shouldInitForAllProjects() {
        Project p1 = new Project("a"); p1.setId(1L);
        Project p2 = new Project("b"); p2.setId(2L);
        when(projectService.getAllProjects()).thenReturn(List.of(p1, p2));
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p1));
        when(projectService.getProjectById(2L)).thenReturn(Optional.of(p2));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        checkInProjectYearlySummaryService.initSummaries();

        verify(repository, times(2)).save(any());
    }
}