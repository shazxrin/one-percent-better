package io.github.shazxrin.onepercentbetter.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {
    @Mock
    private GitHub gitHub;

    @Mock
    private GHRepository mockRepo;

    @Mock
    private PagedIterable<GHCommit> mockCommitsIterable;

    @InjectMocks
    private GitHubService gitHubService;

    private static final String USERNAME = "testuser";
    private static final String REPOSITORY = "testrepo";
    private static final String FULL_REPOSITORY_NAME = String.format("%s/%s", USERNAME, REPOSITORY);
    private static final Date TODAY = Date.from(Instant.now());
    private static final Date YESTERDAY = Date.from(Instant.now().minus(Duration.ofDays(1)));

    @Test
    void testGetCommitCountTodayForRepository_whenRepositoryNameIsFormatted_shouldUseCorrectFormat() throws IOException {
        // Given
        when(gitHub.getRepository(eq(FULL_REPOSITORY_NAME))).thenReturn(mockRepo);
        when(mockRepo.listCommits()).thenReturn(mockCommitsIterable);
        when(mockCommitsIterable.toList()).thenReturn(List.of());

        // When
        gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        verify(gitHub).getRepository(eq(FULL_REPOSITORY_NAME));
    }

    @Test
    void testGetCommitCountTodayForRepository_whenCommitsExistToday_shouldReturnCorrectCount() throws IOException {
        // Given
        LocalDate today = LocalDate.now();

        GHCommit todayCommit1 = mock(GHCommit.class);
        GHCommit todayCommit2 = mock(GHCommit.class);
        GHCommit yesterdayCommit = mock(GHCommit.class);

        when(todayCommit1.getCommitDate()).thenReturn(TODAY);
        when(todayCommit2.getCommitDate()).thenReturn(TODAY);
        when(yesterdayCommit.getCommitDate()).thenReturn(YESTERDAY);

        when(gitHub.getRepository(eq(FULL_REPOSITORY_NAME))).thenReturn(mockRepo);
        when(mockRepo.listCommits()).thenReturn(mockCommitsIterable);
        when(mockCommitsIterable.toList()).thenReturn(List.of(todayCommit1, todayCommit2, yesterdayCommit));

        // When
        int count = gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        assertEquals(2, count);
    }

    @Test
    void testGetCommitCountTodayForRepository_whenNoCommitsToday_shouldReturnZero() throws IOException {
        // Given
        GHCommit yesterdayCommit1 = mock(GHCommit.class);
        GHCommit yesterdayCommit2 = mock(GHCommit.class);

        when(yesterdayCommit1.getCommitDate()).thenReturn(YESTERDAY);
        when(yesterdayCommit2.getCommitDate()).thenReturn(YESTERDAY);

        when(gitHub.getRepository(eq(FULL_REPOSITORY_NAME))).thenReturn(mockRepo);
        when(mockRepo.listCommits()).thenReturn(mockCommitsIterable);
        when(mockCommitsIterable.toList()).thenReturn(List.of(yesterdayCommit1, yesterdayCommit2));

        // When
        int count = gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        assertEquals(0, count);
    }

    @Test
    void testGetCommitCountTodayForRepository_whenNoCommits_shouldReturnZero() throws IOException {
        // Given
        when(gitHub.getRepository(eq(FULL_REPOSITORY_NAME))).thenReturn(mockRepo);
        when(mockRepo.listCommits()).thenReturn(mockCommitsIterable);
        when(mockCommitsIterable.toList()).thenReturn(List.of());

        // When
        int count = gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        assertEquals(0, count);
    }

    @Test
    void testGetCommitCountTodayForRepository_whenIOExceptionOccurs_shouldHandleExceptionAndReturnZero() throws IOException {
        // Given
        when(gitHub.getRepository(FULL_REPOSITORY_NAME)).thenThrow(new IOException("Repository not found"));

        // When
        int count = gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        assertEquals(0, count);
    }
}
