package io.github.shazxrin.onepercentbetter.checkin.summary.service;

import io.github.shazxrin.onepercentbetter.checkin.core.exception.CheckInProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.service.CheckInProjectService;
import io.github.shazxrin.onepercentbetter.checkin.summary.model.CheckInProjectWeeklySummary;
import io.github.shazxrin.onepercentbetter.checkin.summary.repository.CheckInProjectWeeklySummaryRepository;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectWeeklySummaryServiceTest {

    @Mock
    private CheckInProjectWeeklySummaryRepository repository;

    @Mock
    private ProjectService projectService;

    @Mock
    private CheckInProjectService checkInProjectService;

    @InjectMocks
    private CheckInProjectWeeklySummaryService service;

    @Test
    void testGetSummary_whenSummaryExists_shouldReturnSummary() {
        long projectId = 1L;
        int year = 2025;
        int week = 31;
        Project project = new Project("repo");
        project.setId(projectId);
        CheckInProjectWeeklySummary summary = new CheckInProjectWeeklySummary(week, year, LocalDate.now(), LocalDate.now().plusDays(6), 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(repository.findByProjectIdAndYearAndWeekNo(projectId, year, week)).thenReturn(Optional.of(summary));

        var result = service.getSummary(projectId, year, week);
        assertEquals(summary, result);
    }

    @Test
    void testGetSummary_whenProjectDoesNotExist_shouldThrow() {
        when(projectService.getProjectById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> service.getSummary(1L, 2025, 1));
    }

    @Test
    void testGetSummary_whenSummaryDoesNotExist_shouldThrow() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(new Project("repo")));
        when(repository.findByProjectIdAndYearAndWeekNo(anyLong(), anyInt(), anyInt())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> service.getSummary(projectId, 2025, 1));
    }

    @Test
    void testCalculateSummaryForWeek_shouldComputeCountsDistributionsAndStreak() {
        long projectId = 1L;
        int year = 2025;
        int weekNo = 31;
        LocalDate start = LocalDate.of(2025, 7, 28).with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);
        Project project = new Project("repo");
        project.setId(projectId);
        CheckInProjectWeeklySummary summary = new CheckInProjectWeeklySummary(weekNo, year, start, end, 0, 0, project);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(repository.findByProjectIdAndYearAndWeekNoWithLock(projectId, year, weekNo)).thenReturn(Optional.of(summary));

        var ci1 = new CheckInProject(LocalDateTime.of(2025, 7, 28, 1, 0), "a", "feat", "msg", project); // Mon
        var ci2 = new CheckInProject(LocalDateTime.of(2025, 7, 29, 1, 30), "b", "bug", "msg", project); // Tue
        var ci3 = new CheckInProject(LocalDateTime.of(2025, 7, 30, 12, 0), "c", null, "msg", project); // Wed null type
        var ci4 = new CheckInProject(LocalDateTime.of(2025, 7, 30, 12, 30), "d", "feat", "msg", project); // Wed
        when(checkInProjectService.getAllCheckInsByProjectBetween(projectId, start, end)).thenReturn(List.of(ci1, ci2, ci3, ci4));

        service.calculateSummaryForWeek(projectId, year, weekNo);

        // verify
        assertEquals(4, summary.getNoOfCheckIns());
        // streak should be 3 (Mon-Tue-Wed consecutive days)
        assertEquals(3, summary.getStreak());
        assertEquals(2, summary.getTypeDistribution().get("feat"));
        assertEquals(1, summary.getTypeDistribution().get("bug"));
        assertEquals(1, summary.getTypeDistribution().get("unknown"));
        assertEquals(2, summary.getHourDistribution().get("12"));
        assertEquals(2, summary.getHourDistribution().get("1"));
        assertEquals(1, summary.getDayDistribution().get("MONDAY"));
        assertEquals(1, summary.getDayDistribution().get("TUESDAY"));
        assertEquals(2, summary.getDayDistribution().get("WEDNESDAY"));
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
        int weekNo = checkIn.getDateTime().get(java.time.temporal.WeekFields.of(DayOfWeek.MONDAY, 4).weekOfYear());
        LocalDate start = checkIn.getDateTime().toLocalDate().with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);

        CheckInProjectWeeklySummary summary = new CheckInProjectWeeklySummary(weekNo, year, start, end, 0, 0, project);
        when(repository.findByProjectIdAndYearAndWeekNoWithLock(projectId, year, weekNo)).thenReturn(Optional.of(summary));

        service.addCheckInToSummary(projectId, 2L);

        assertEquals(1, summary.getNoOfCheckIns());
        assertEquals(1, summary.getTypeDistribution().get("unknown"));
        assertEquals(1, summary.getHourDistribution().get("9"));
        assertEquals(1, summary.getDayDistribution().get("THURSDAY"));
        // streak computed from day distribution; only one day => 1
        assertEquals(1, summary.getStreak());
        verify(repository, times(1)).save(summary);
    }

    @Test
    void testAddCheckInToSummary_whenProjectDoesNotExist_shouldThrow() {
        when(projectService.getProjectById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> service.addCheckInToSummary(1L, 2L));
    }

    @Test
    void testAddCheckInToSummary_whenCheckInDoesNotExist_shouldThrow() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(new Project("repo")));
        when(checkInProjectService.getCheckIn(anyLong())).thenReturn(Optional.empty());
        assertThrows(CheckInProjectNotFoundException.class, () -> service.addCheckInToSummary(projectId, 2L));
    }

    @Test
    void testInitSummary_shouldCreateWeeklySummariesForYearAndSaveAll() {
        long projectId = 1L;
        Project project = new Project("repo");
        project.setId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        ArgumentCaptor<List<CheckInProjectWeeklySummary>> captor = ArgumentCaptor.forClass(List.class);
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        service.initSummary(projectId);

        verify(repository, times(1)).saveAll(captor.capture());
        List<CheckInProjectWeeklySummary> saved = captor.getValue();
        assertNotNull(saved);
        assertTrue(saved.size() >= 52 && saved.size() <= 54); // ISO week years can vary
        for (CheckInProjectWeeklySummary s : saved) {
            assertEquals(LocalDate.now().getYear(), s.getYear());
            assertEquals(s.getStartDate().with(DayOfWeek.MONDAY), s.getStartDate());
            assertEquals(s.getStartDate().plusDays(6), s.getEndDate());
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

        service.initSummaries();

        // saveAll should be called once per project
        verify(repository, times(2)).saveAll(anyList());
    }
}
