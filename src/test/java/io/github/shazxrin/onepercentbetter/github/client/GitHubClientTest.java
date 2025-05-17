package io.github.shazxrin.onepercentbetter.github.client;

import io.github.shazxrin.onepercentbetter.github.dto.commit.Commit;
import io.github.shazxrin.onepercentbetter.github.exception.GitHubException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GitHubClientTest {
    private static GitHubClient gitHubClient;
    
    private static final String WELL_KNOWN_OWNER = "shazxrin";
    private static final String WELL_KNOWN_REPOSITORY = "website";
    private static final String NONEXISTENT_REPOSITORY = "this-repo-does-not-exist";


    @BeforeAll
    static void setUp() {
        RestClient gitHubRestClient = RestClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeaders(httpHeaders -> {
                httpHeaders.add("Accept", "application/vnd.github+json");
                httpHeaders.add("X-GitHub-Api-Version", "2022-11-28");
            })
            .build();

        gitHubClient = new GitHubClient(gitHubRestClient);
    }

    @Test
    void testGetCommits_withDateRange_shouldReturnCommits() {
        // Given
        OffsetDateTime since = LocalDate.of(2024, 1, 1)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
        OffsetDateTime until = LocalDate.of(2024, 1, 31)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
        
        // When
        List<Commit> commits = gitHubClient.getCommits(
                WELL_KNOWN_OWNER, 
                WELL_KNOWN_REPOSITORY, 
                since, 
                until
        );
        
        // Then
        assertFalse(commits.isEmpty());
        assertEquals(2, commits.size());
    }

    @Test
    void testGetCommits_withDateRangeWithinADay_shouldReturnCommits() {
        // Given
        OffsetDateTime since = ZonedDateTime.of(LocalDate.of(2024, 1, 27), LocalTime.MIN, ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .toOffsetDateTime();
        OffsetDateTime until = ZonedDateTime.of(
                LocalDate.of(2024, 1, 27),
                LocalTime.MAX,
                ZoneId.systemDefault()
            )
            .withZoneSameInstant(ZoneOffset.UTC)
            .toOffsetDateTime();

        // When
        List<Commit> commits = gitHubClient.getCommits(
            WELL_KNOWN_OWNER,
            WELL_KNOWN_REPOSITORY,
            since,
            until
        );

        // Then
        assertFalse(commits.isEmpty());
        assertEquals(1, commits.size());
    }
    
    @Test
    void testGetCommits_withEmptyDateRange_shouldReturnNoCommits() {
        // Given
        OffsetDateTime since = LocalDate.of(2023, 1, 1)
            .atStartOfDay()
            .atOffset(ZoneOffset.UTC);
        OffsetDateTime until = LocalDate.of(2023, 1, 31)
            .atStartOfDay()
            .atOffset(ZoneOffset.UTC);

        // When
        List<Commit> commits = gitHubClient.getCommits(
                WELL_KNOWN_OWNER, 
                WELL_KNOWN_REPOSITORY, 
                since, 
                until
        );
        
        // Then
        assertNotNull(commits);
        assertTrue(commits.isEmpty());
    }
    
    @Test
    void testGetCommits_withNonexistentRepository_shouldThrowException() {
        // Given
        OffsetDateTime since = LocalDate.now().minusDays(30)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
        OffsetDateTime until = LocalDate.now()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
        
        // When & Then
        assertThrows(
            GitHubException.class,
            () -> gitHubClient.getCommits(
                WELL_KNOWN_OWNER, 
                NONEXISTENT_REPOSITORY, 
                since, 
                until
            )
        );
    }
}
