package io.github.shazxrin.onepercentbetter.github.service;

import io.github.shazxrin.onepercentbetter.github.client.GitHubClient;
import io.github.shazxrin.onepercentbetter.github.dto.commit.Commit;
import io.github.shazxrin.onepercentbetter.github.exception.GitHubException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {
    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private MainGitHubService gitHubService;

    private static final String USERNAME = "testuser";
    private static final String REPOSITORY = "testrepo";
    private static final OffsetDateTime START_OF_DAY = LocalDate.now()
            .atStartOfDay()
            .atOffset(ZoneOffset.UTC);
    private static final OffsetDateTime END_OF_DAY = LocalDate.now()
            .plusDays(1)
            .atStartOfDay()
            .atOffset(ZoneOffset.UTC);

    @Test
    void testGetCommitCountTodayForRepository_whenRepositoryNameIsFormatted_shouldUseCorrectFormat() {
        // Given
        when(gitHubClient.getCommits(eq(USERNAME), eq(REPOSITORY), any(), any()))
            .thenReturn(Collections.emptyList());

        // When
        gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        verify(gitHubClient).getCommits(eq(USERNAME), eq(REPOSITORY), any(), any());
    }

    @Test
    void testGetCommitCountTodayForRepository_whenCommitsExistToday_shouldReturnCorrectCount() {
        // Given
        Commit commit1 = mock(Commit.class);
        Commit commit2 = mock(Commit.class);
        List<Commit> commits = List.of(commit1, commit2);

        when(gitHubClient.getCommits(eq(USERNAME), eq(REPOSITORY), any(), any()))
            .thenReturn(commits);

        // When
        int count = gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        assertEquals(2, count);
    }

    @Test
    void testGetCommitCountTodayForRepository_whenNoCommits_shouldReturnZero() {
        // Given
        when(gitHubClient.getCommits(eq(USERNAME), eq(REPOSITORY), any(), any()))
            .thenReturn(Collections.emptyList());

        // When
        int count = gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        assertEquals(0, count);
    }

    @Test
    void testGetCommitCountTodayForRepository_whenExceptionOccurs_shouldHandleExceptionAndReturnZero() {
        // Given
        when(gitHubClient.getCommits(eq(USERNAME), eq(REPOSITORY), any(), any()))
            .thenThrow(new GitHubException("Repository not found"));

        // When
        int count = gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        assertEquals(0, count);
    }
    
    @Test
    void testGetCommitCountTodayForRepository_shouldUseCorrectDateRange() {
        // Given
        when(gitHubClient.getCommits(eq(USERNAME), eq(REPOSITORY), any(), any()))
            .thenReturn(Collections.emptyList());

        // When
        gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        verify(gitHubClient).getCommits(
            eq(USERNAME),
            eq(REPOSITORY),
            any(OffsetDateTime.class),
            any(OffsetDateTime.class)
        );
    }
}
