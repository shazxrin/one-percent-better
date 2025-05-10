package io.github.shazxrin.onepercentbetter.service.github;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainGitHubService implements GitHubService {
    private static final Logger log = LoggerFactory.getLogger(MainGitHubService.class);

    private final GitHub gitHub;

    @Autowired
    private MainGitHubService(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    private String createRepositoryName(String username, String repository) {
        return String.format("%s/%s", username, repository);
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
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
