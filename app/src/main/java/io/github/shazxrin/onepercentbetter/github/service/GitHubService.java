package io.github.shazxrin.onepercentbetter.github.service;

import io.github.shazxrin.onepercentbetter.github.client.GitHubClient;
import io.github.shazxrin.onepercentbetter.github.exception.GitHubException;
import io.github.shazxrin.onepercentbetter.github.model.Commit;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Observed
@Service
public class GitHubService {
    private static final Logger log = LoggerFactory.getLogger(GitHubService.class);

    private final GitHubClient gitHubClient;

    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    private OffsetDateTime getStartOfDay(LocalDate date) {
        return ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .toOffsetDateTime();

    }

    private OffsetDateTime getEndOfDay(LocalDate date) {
        return ZonedDateTime.of(date, LocalTime.MAX, ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .toOffsetDateTime();
    }

    public List<Commit> getCommitsForRespositoryOnDate(String username, String repository, LocalDate date) {
        try {
            return gitHubClient.getCommits(
                username,
                repository,
                getStartOfDay(date),
                getEndOfDay(date)
            );
        } catch (GitHubException ex) {
            log.error("Error getting commit count today.", ex);
            return List.of();
        }
    }
}
