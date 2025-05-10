package io.github.shazxrin.onepercentbetter.service.github;

public interface GitHubService {
    int getCommitCountTodayForRepository(String username, String repository);
}
