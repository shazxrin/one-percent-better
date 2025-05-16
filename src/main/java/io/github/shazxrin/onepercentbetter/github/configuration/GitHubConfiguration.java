package io.github.shazxrin.onepercentbetter.github.configuration;

import java.io.IOException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitHubConfiguration {
    private final GitHubProperties gitHubProperties;

    public GitHubConfiguration(GitHubProperties gitHubProperties) {
        this.gitHubProperties = gitHubProperties;
    }

    @Bean
    public GitHub github() throws IOException {
        return new GitHubBuilder()
            .withOAuthToken(gitHubProperties.getToken(), gitHubProperties.getUsername())
            .build();
    }
}
