package io.github.shazxrin.onepercentbetter.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    private String createRepositoryName(String username, String repository) {
        return String.format("%s/%s", username, repository);
    }

    @Test
    void testGetCommitCountTodayForRepository_whenRepositoryNameIsFormatted_shouldUseCorrectFormat() throws IOException {
        // Given
        when(gitHub.getRepository(eq(createRepositoryName(USERNAME, REPOSITORY)))).thenReturn(mockRepo);
        when(mockRepo.listCommits()).thenReturn(mockCommitsIterable);
        when(mockCommitsIterable.toList()).thenReturn(List.of());

        // When
        gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        verify(gitHub).getRepository(eq(createRepositoryName(USERNAME, REPOSITORY)));
    }

    @Test
    void testGetCommitCountTodayForRepository_whenCommitsExistToday_shouldReturnCorrectCount() throws IOException {
        // Given
        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date yesterdayDate = Date.from(today.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        GHCommit todayCommit1 = mock(GHCommit.class);
        GHCommit todayCommit2 = mock(GHCommit.class);
        GHCommit yesterdayCommit = mock(GHCommit.class);

        when(todayCommit1.getCommitDate()).thenReturn(todayDate);
        when(todayCommit2.getCommitDate()).thenReturn(todayDate);
        when(yesterdayCommit.getCommitDate()).thenReturn(yesterdayDate);

        when(gitHub.getRepository(eq(createRepositoryName(USERNAME, REPOSITORY)))).thenReturn(mockRepo);
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
        LocalDate today = LocalDate.now();
        Date yesterdayDate = Date.from(today.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        GHCommit yesterdayCommit1 = mock(GHCommit.class);
        GHCommit yesterdayCommit2 = mock(GHCommit.class);

        when(yesterdayCommit1.getCommitDate()).thenReturn(yesterdayDate);
        when(yesterdayCommit2.getCommitDate()).thenReturn(yesterdayDate);

        when(gitHub.getRepository(eq(createRepositoryName(USERNAME, REPOSITORY)))).thenReturn(mockRepo);
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
        when(gitHub.getRepository(eq(createRepositoryName(USERNAME, REPOSITORY)))).thenReturn(mockRepo);
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
        when(gitHub.getRepository(createRepositoryName(USERNAME, REPOSITORY))).thenThrow(new IOException("Repository not found"));

        // When
        int count = gitHubService.getCommitCountTodayForRepository(USERNAME, REPOSITORY);

        // Then
        assertEquals(0, count);
    }
}
