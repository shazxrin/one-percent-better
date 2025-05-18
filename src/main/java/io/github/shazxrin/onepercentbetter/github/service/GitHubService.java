package io.github.shazxrin.onepercentbetter.github.service;

import java.time.LocalDate;

public interface GitHubService {
    int getCommitCountForRepositoryOnDate(String username, String repository, LocalDate date);
}
