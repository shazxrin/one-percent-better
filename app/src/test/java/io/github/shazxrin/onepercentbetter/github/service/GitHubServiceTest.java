package io.github.shazxrin.onepercentbetter.github.service;

import io.github.shazxrin.onepercentbetter.github.client.GitHubClient;
import io.github.shazxrin.onepercentbetter.github.exception.GitHubException;
import io.github.shazxrin.onepercentbetter.github.model.Commit;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {
    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubService gitHubService;

    private static final String USERNAME = "testuser";
    private static final String REPOSITORY = "testrepo";

    @Test
    void testGetCommitCountForRepositoryOnDate_whenCommitsExist_shouldReturnCorrect() {
        // Given
        Commit commit1 = mock(Commit.class);
        Commit commit2 = mock(Commit.class);
        List<Commit> commits = List.of(commit1, commit2);

        when(gitHubClient.getCommits(eq(USERNAME), eq(REPOSITORY), any(), any()))
            .thenReturn(commits);

        // When
        List<Commit> results = gitHubService.getCommitsForRespositoryOnDate(USERNAME, REPOSITORY, LocalDate.now());

        // Then
        assertEquals(2, results.size());
    }

    @Test
    void testGetCommitCountForRepositoryOnDate_whenNoCommits_shouldReturnZero() {
        // Given
        when(gitHubClient.getCommits(eq(USERNAME), eq(REPOSITORY), any(), any()))
            .thenReturn(Collections.emptyList());

        // When
        List<Commit> results = gitHubService.getCommitsForRespositoryOnDate(USERNAME, REPOSITORY, LocalDate.now());

        // Then
        assertEquals(0, results.size());
    }

    @Test
    void testGetCommitCountForRepositoryOnDate_whenExceptionOccurs_shouldHandleExceptionAndReturnEmptyList() {
        // Given
        when(gitHubClient.getCommits(eq(USERNAME), eq(REPOSITORY), any(), any()))
            .thenThrow(new GitHubException("Repository not found"));

        // When
        List<Commit> results = gitHubService.getCommitsForRespositoryOnDate(USERNAME, REPOSITORY, LocalDate.now());

        // Then
        assertEquals(0, results.size());
    }
}
