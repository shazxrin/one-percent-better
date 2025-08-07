package io.github.shazxrin.onepercentbetter.checkin.core.service;

import io.github.shazxrin.onepercentbetter.checkin.core.event.CheckInProjectAddedEvent;
import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.checkin.core.repository.CheckInProjectRepository;
import io.github.shazxrin.onepercentbetter.github.model.Commit;
import io.github.shazxrin.onepercentbetter.github.model.CommitDetail;
import io.github.shazxrin.onepercentbetter.github.service.GitHubService;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInProjectServiceTest {

    @Mock
    private CheckInProjectRepository checkInProjectRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private GitHubService gitHubService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CheckInProjectService checkInProjectService;

    @Test
    void testCheckIn_whenProjectHasNewCommits_shouldCreateNewCheckInsAndFireEvent() {
        long projectId = 1L;
        LocalDate date = LocalDate.now();
        Project project = new Project();
        project.setId(projectId);
        project.setName("owner/repo");

        CommitDetail commitDetail = mock(CommitDetail.class);
        when(commitDetail.message()).thenReturn("feat: add feature");
        Commit commit = mock(Commit.class);
        when(commit.sha()).thenReturn("abc123");
        when(commit.commit()).thenReturn(commitDetail);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(gitHubService.getCommitsForRespositoryOnDate("owner", "repo", date)).thenReturn(List.of(commit));
        when(checkInProjectRepository.existsByProjectIdAndHash(projectId, "abc123")).thenReturn(false);

        checkInProjectService.checkIn(projectId, date);

        ArgumentCaptor<CheckInProject> checkInCaptor = ArgumentCaptor.forClass(CheckInProject.class);
        verify(checkInProjectRepository).save(checkInCaptor.capture());
        CheckInProject saved = checkInCaptor.getValue();
        assertEquals(date, saved.getDate());
        assertEquals("abc123", saved.getHash());
        assertEquals("feat", saved.getType());
        assertEquals("add feature", saved.getMessage());
        assertEquals(project, saved.getProject());

        verify(applicationEventPublisher).publishEvent(any(CheckInProjectAddedEvent.class));
    }

    @Test
    void testCheckIn_whenProjectHasNoNewCommits_shouldNotCreateNewCheckInsAndNotFireEvent() {
        long projectId = 2L;
        LocalDate date = LocalDate.now();
        Project project = new Project();
        project.setId(projectId);
        project.setName("owner/repo");

        Commit commit = mock(Commit.class);
        when(commit.sha()).thenReturn("def456");

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(gitHubService.getCommitsForRespositoryOnDate("owner", "repo", date)).thenReturn(List.of(commit));
        when(checkInProjectRepository.existsByProjectIdAndHash(projectId, "def456")).thenReturn(true);

        checkInProjectService.checkIn(projectId, date);

        verify(checkInProjectRepository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void testCheckInInterval_whenProjectHasNewCommitsForMultipleDays_shouldCreateNewCheckInsAndFireEvents() {
        long projectId = 3L;
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now();
        Project project = new Project();
        project.setId(projectId);
        project.setName("owner/repo");

        CommitDetail commitInfo1 = mock(CommitDetail.class);
        when(commitInfo1.message()).thenReturn("chore: update");
        Commit commit1 = mock(Commit.class);
        when(commit1.sha()).thenReturn("sha1");
        when(commit1.commit()).thenReturn(commitInfo1);

        CommitDetail commitInfo2 = mock(CommitDetail.class);
        when(commitInfo2.message()).thenReturn("docs: update docs");
        Commit commit2 = mock(Commit.class);
        when(commit2.sha()).thenReturn("sha2");
        when(commit2.commit()).thenReturn(commitInfo2);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(gitHubService.getCommitsForRespositoryOnDate("owner", "repo", from)).thenReturn(List.of(commit1));
        when(gitHubService.getCommitsForRespositoryOnDate("owner", "repo", to)).thenReturn(List.of(commit2));
        when(checkInProjectRepository.existsByProjectIdAndHash(projectId, "sha1")).thenReturn(false);
        when(checkInProjectRepository.existsByProjectIdAndHash(projectId, "sha2")).thenReturn(false);

        checkInProjectService.checkInInterval(projectId, from, to);

        verify(checkInProjectRepository, times(2)).save(any());
        verify(applicationEventPublisher, times(2)).publishEvent(any(CheckInProjectAddedEvent.class));
    }

    @Test
    void testCheckInAll_whenProjectHasNewCommits_shouldCreateNewCheckInsAndFireEvents() {
        LocalDate date = LocalDate.now();
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("owner1/repo1");
        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("owner2/repo2");

        CommitDetail commitInfo1 = mock(CommitDetail.class);
        when(commitInfo1.message()).thenReturn("feat: add 1");
        Commit commit1 = mock(Commit.class);
        when(commit1.sha()).thenReturn("sha1");
        when(commit1.commit()).thenReturn(commitInfo1);

        CommitDetail commitInfo2 = mock(CommitDetail.class);
        when(commitInfo2.message()).thenReturn("fix: fix 2");
        Commit commit2 = mock(Commit.class);
        when(commit2.sha()).thenReturn("sha2");
        when(commit2.commit()).thenReturn(commitInfo2);

        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));
        when(gitHubService.getCommitsForRespositoryOnDate("owner1", "repo1", date)).thenReturn(List.of(commit1));
        when(gitHubService.getCommitsForRespositoryOnDate("owner2", "repo2", date)).thenReturn(List.of(commit2));
        when(checkInProjectRepository.existsByProjectIdAndHash(1L, "sha1")).thenReturn(false);
        when(checkInProjectRepository.existsByProjectIdAndHash(2L, "sha2")).thenReturn(false);

        checkInProjectService.checkInAll(date);

        verify(checkInProjectRepository, times(2)).save(any());
        verify(applicationEventPublisher, times(2)).publishEvent(any(CheckInProjectAddedEvent.class));
    }

    @Test
    void testCheckInAllInterval_whenProjectHasNewCommitsForMultipleDays_shouldCreateNewCheckInsAndFireEvents() {
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now();
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("owner1/repo1");
        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("owner2/repo2");

        CommitDetail commitInfo1 = mock(CommitDetail.class);
        when(commitInfo1.message()).thenReturn("feat: add 1");
        Commit commit1 = mock(Commit.class);
        when(commit1.sha()).thenReturn("sha1");
        when(commit1.commit()).thenReturn(commitInfo1);

        CommitDetail commitInfo2 = mock(CommitDetail.class);
        when(commitInfo2.message()).thenReturn("fix: fix 2");
        Commit commit2 = mock(Commit.class);
        when(commit2.sha()).thenReturn("sha2");
        when(commit2.commit()).thenReturn(commitInfo2);

        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));
        when(gitHubService.getCommitsForRespositoryOnDate("owner1", "repo1", from)).thenReturn(List.of(commit1));
        when(gitHubService.getCommitsForRespositoryOnDate("owner2", "repo2", from)).thenReturn(List.of(commit2));
        when(gitHubService.getCommitsForRespositoryOnDate("owner1", "repo1", to)).thenReturn(List.of(commit1));
        when(gitHubService.getCommitsForRespositoryOnDate("owner2", "repo2", to)).thenReturn(List.of(commit2));
        when(checkInProjectRepository.existsByProjectIdAndHash(1L, "sha1")).thenReturn(false);
        when(checkInProjectRepository.existsByProjectIdAndHash(2L, "sha2")).thenReturn(false);

        checkInProjectService.checkInAllInterval(from, to);

        verify(checkInProjectRepository, times(4)).save(any());
        verify(applicationEventPublisher, times(4)).publishEvent(any(CheckInProjectAddedEvent.class));
    }
}