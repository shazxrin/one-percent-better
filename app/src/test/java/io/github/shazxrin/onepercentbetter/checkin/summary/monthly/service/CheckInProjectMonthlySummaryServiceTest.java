package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.model.CheckInProjectMonthlySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.repository.CheckInProjectMonthlySummaryRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectMonthlySummaryServiceTest {

    @Mock
    private CheckInProjectMonthlySummaryRepository repository;

    @Mock
    private ProjectService projectService;

    @Mock
    private CheckInProjectService checkInProjectService;

    @InjectMocks
    private CheckInProjectMonthlySummaryService checkInProjectMonthlySummaryService;

    @Test
    void testGetSummary_whenSummaryExists_shouldReturnSummary() {
        long projectId = 1L;
        int year = 2025;
        int month = 7;
        Project project = new Project("repo");
        project.setId(projectId);
        LocalDate start = LocalDate.of(2025, 7, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        CheckInProjectMonthlySummary summary = new CheckInProjectMonthlySummary(year, month, start, end, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(repository.findByProjectIdAndYearAndMonthNo(projectId, year, month)).thenReturn(Optional.of(summary));

        var result = checkInProjectMonthlySummaryService.getSummary(projectId, year, month);
        assertEquals(summary, result);
    }

    @Test
    void testGetSummary_whenProjectDoesNotExist_shouldThrow() {
        when(projectService.getProjectById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> checkInProjectMonthlySummaryService.getSummary(1L, 2025, 7));
    }

    @Test
    void testGetSummary_whenSummaryDoesNotExist_shouldThrow() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(new Project("repo")));
        when(repository.findByProjectIdAndYearAndMonthNo(anyLong(), anyInt(), anyInt())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> checkInProjectMonthlySummaryService.getSummary(projectId, 2025, 7));
    }

    @Test
    void testCalculateSummaryForMonth_shouldComputeCountsDistributionsAndStreak() {
        long projectId = 1L;
        int year = 2025;
        int monthNo = 7; // July
        LocalDate start = LocalDate.of(2025, 7, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        Project project = new Project("repo");
        project.setId(projectId);
        CheckInProjectMonthlySummary summary = new CheckInProjectMonthlySummary(year, monthNo, start, end, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(repository.findByProjectIdAndYearAndMonthNoWithLock(projectId, year, monthNo)).thenReturn(Optional.of(summary));

        var ci1 = new CheckInProject(LocalDateTime.of(2025, 7, 1, 1, 0), "a", "feat", "msg", project);
        var ci2 = new CheckInProject(LocalDateTime.of(2025, 7, 2, 1, 30), "b", "bug", "msg", project);
        var ci3 = new CheckInProject(LocalDateTime.of(2025, 7, 3, 12, 0), "c", null, "msg", project);
        var ci4 = new CheckInProject(LocalDateTime.of(2025, 7, 3, 12, 30), "d", "feat", "msg", project);
        when(checkInProjectService.getAllCheckInsByProjectBetween(projectId, start, end)).thenReturn(List.of(ci1, ci2, ci3, ci4));

        checkInProjectMonthlySummaryService.calculateSummaryForMonth(projectId, year, monthNo);

        assertEquals(4, summary.getNoOfCheckIns());
        assertEquals(3, summary.getStreak());
        assertEquals(2, summary.getTypeDistribution().get("feat"));
        assertEquals(1, summary.getTypeDistribution().get("bug"));
        assertEquals(1, summary.getTypeDistribution().get("unknown"));
        assertEquals(2, summary.getHourDistribution().get("12"));
        assertEquals(2, summary.getHourDistribution().get("1"));
        // Day distribution should have counts for whatever days those dates are; we check total adds up
        int totalDays = summary.getDateDistribution().values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(4, totalDays);
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
        int monthNo = checkIn.getDateTime().getMonthValue();
        LocalDate start = checkIn.getDateTime().toLocalDate().withDayOfMonth(1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        CheckInProjectMonthlySummary summary = new CheckInProjectMonthlySummary(year, monthNo, start, end, 0, 0, project);
        when(repository.findByProjectIdAndYearAndMonthNoWithLock(projectId, year, monthNo)).thenReturn(Optional.of(summary));

        checkInProjectMonthlySummaryService.addCheckInToSummary(projectId, 2L);

        assertEquals(1, summary.getNoOfCheckIns());
        assertEquals(1, summary.getTypeDistribution().get("unknown"));
        assertEquals(1, summary.getHourDistribution().get("9"));
        int totalDays = summary.getDateDistribution().values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(1, totalDays);
        assertEquals(1, summary.getStreak());
        verify(repository, times(1)).save(summary);
    }

    @Test
    void testAddCheckInToSummary_whenProjectDoesNotExist_shouldThrow() {
        when(projectService.getProjectById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> checkInProjectMonthlySummaryService.addCheckInToSummary(1L, 2L));
    }

    @Test
    void testAddCheckInToSummary_whenCheckInDoesNotExist_shouldThrow() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(new Project("repo")));
        when(checkInProjectService.getCheckIn(anyLong())).thenReturn(Optional.empty());
        assertThrows(CheckInProjectNotFoundException.class, () -> checkInProjectMonthlySummaryService.addCheckInToSummary(projectId, 2L));
    }

    @Test
    void testInitSummary_shouldCreateMonthlySummariesForYearAndSaveAll() {
        long projectId = 1L;
        Project project = new Project("repo");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        ArgumentCaptor<List<CheckInProjectMonthlySummary>> captor = ArgumentCaptor.forClass(List.class);
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        checkInProjectMonthlySummaryService.initSummary(projectId);

        verify(repository, times(1)).saveAll(captor.capture());
        List<CheckInProjectMonthlySummary> saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(12, saved.size());
        for (CheckInProjectMonthlySummary s : saved) {
            assertEquals(LocalDate.now().getYear(), s.getYear());
            assertEquals(s.getStartDate().withDayOfMonth(1), s.getStartDate());
            assertEquals(s.getStartDate().withDayOfMonth(s.getStartDate().lengthOfMonth()), s.getEndDate());
        }
    }

    @Test
    void testInitSummaries_shouldInitForAllProjects() {
        Project p1 = new Project("a"); p1.setId(1L);
        Project p2 = new Project("b"); p2.setId(2L);
        when(projectService.getAllProjects()).thenReturn(List.of(p1, p2));
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p1));
        when(projectService.getProjectById(2L)).thenReturn(Optional.of(p2));
        when(repository.saveAll(anyList())).thenReturn(List.of());

        checkInProjectMonthlySummaryService.initSummaries();

        verify(repository, times(2)).saveAll(anyList());
    }
}
