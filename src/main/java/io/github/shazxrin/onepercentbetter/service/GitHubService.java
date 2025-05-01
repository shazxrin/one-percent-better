package io.github.shazxrin.onepercentbetter.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GitHubService {
    private final GitHub gitHub;

    private String createRepositoryName(String username, String repository) {
        return String.format("%s/%s", username, repository);
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public int getCommitCountTodayForRepository(String username, String repository) {
        int count = 0;
        try {
            List<GHCommit> commits = gitHub.getRepository(createRepositoryName(username, repository))
                .listCommits()
                .toList();

            for (GHCommit commit : commits) {
                LocalDate commitDate = convertToLocalDate(commit.getCommitDate());
                if (commitDate.isEqual(LocalDate.now())) {
                    count++;
                }
            }
        } catch (IOException ex) {
            log.error("Failed to get commit count for {}/{}", username, repository, ex);
        }
        log.info("Commit count for {}/{} is {}", username, repository, count);
        return count;
    }
}
