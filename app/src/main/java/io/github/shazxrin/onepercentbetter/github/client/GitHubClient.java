package io.github.shazxrin.onepercentbetter.github.client;

import io.github.shazxrin.onepercentbetter.github.dto.commit.Commit;
import io.github.shazxrin.onepercentbetter.github.exception.GitHubException;
import io.micrometer.observation.annotation.Observed;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Observed
@Component
public class GitHubClient {
    private final RestClient gitHubRestClient;
    private static final ParameterizedTypeReference<List<Commit>> LIST_COMMIT_TYPE = new ParameterizedTypeReference<>() {};

    public GitHubClient(RestClient gitHubRestClient) {
        this.gitHubRestClient = gitHubRestClient;
    }

    public List<Commit> getCommits(String owner, String repository, OffsetDateTime since, OffsetDateTime until) {
        try {
            return gitHubRestClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(String.format("/repos/%s/%s/commits", owner, repository));
                    uriBuilder.queryParam("per_page", 100);
                    uriBuilder.queryParam("since", since);
                    uriBuilder.queryParam("until", until);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(LIST_COMMIT_TYPE);
        } catch (RestClientException ex) {
            throw new GitHubException("Error communicating with GitHub API.", ex);
        }
    }
}
