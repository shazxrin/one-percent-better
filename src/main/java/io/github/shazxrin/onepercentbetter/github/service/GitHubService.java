package io.github.shazxrin.onepercentbetter.github.service;

public interface GitHubService {
    int getCommitCountTodayForRepository(String username, String repository);
}
