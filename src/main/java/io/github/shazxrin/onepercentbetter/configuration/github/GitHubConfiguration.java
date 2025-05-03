package io.github.shazxrin.onepercentbetter.configuration.github;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class GitHubConfiguration {
    private final GitHubProperties gitHubProperties;

    @Bean
    public GitHub github() throws IOException {
        return new GitHubBuilder()
            .withOAuthToken(gitHubProperties.getToken(), gitHubProperties.getUsername())
            .build();
    }
}
