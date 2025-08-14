package io.github.shazxrin.onepercentbetter.github.configuration;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GitHubConfiguration {
    private final GitHubProperties gitHubProperties;

    public GitHubConfiguration(GitHubProperties gitHubProperties) {
        this.gitHubProperties = gitHubProperties;
    }

    @Bean
    public RestClient gitHubRestClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
            .baseUrl("https://api.github.com")
            .defaultHeaders(httpHeaders -> {
                httpHeaders.add("Accept", "application/vnd.github+json");
                httpHeaders.add("Authorization", "Bearer " + gitHubProperties.getToken());
                httpHeaders.add("X-GitHub-Api-Version", "2022-11-28");
            })
            .build();
    }
}
