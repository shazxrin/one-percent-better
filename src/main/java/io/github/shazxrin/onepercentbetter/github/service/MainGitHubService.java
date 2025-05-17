package io.github.shazxrin.onepercentbetter.github.service;

import io.github.shazxrin.onepercentbetter.github.client.GitHubClient;
import io.github.shazxrin.onepercentbetter.github.dto.commit.Commit;
import io.github.shazxrin.onepercentbetter.github.exception.GitHubException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MainGitHubService implements GitHubService {
    private static final Logger log = LoggerFactory.getLogger(MainGitHubService.class);

    private final GitHubClient gitHubClient;

    private MainGitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    private OffsetDateTime getStartOfDay() {
        return LocalDate.now()
            .atStartOfDay()
            .atOffset(ZoneOffset.UTC);
    }

    private OffsetDateTime getEndOfDay() {
        return LocalDate.now()
            .atTime(23, 59, 59)
            .atOffset(ZoneOffset.UTC);
    }

    @Override
    public int getCommitCountTodayForRepository(String username, String repository) {
        try {
            List<Commit> commits = gitHubClient.getCommits(
                username,
                repository,
                getStartOfDay(),
                getEndOfDay()
            );
            return commits.size();
        } catch (GitHubException ex) {
            log.error("Error getting commit count today.", ex);
            return 0;
        }
    }
}
