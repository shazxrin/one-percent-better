package io.github.shazxrin.onepercentbetter.github.dto.commit;

import java.net.URI;
import java.time.OffsetDateTime;

public record CommitGitHubUser(
    String login,
    int id,
    String nodeId,
    URI avatarUrl,
    String gravatarId,
    URI url,
    URI htmlUrl,
    URI followersUrl,
    String followingUrl,
    String gistsUrl,
    String starredUrl,
    URI subscriptionsUrl,
    URI organizationsUrl,
    URI reposUrl,
    String eventsUrl,
    URI receivedEventsUrl,
    String type,
    boolean siteAdmin,
    OffsetDateTime starredAt,
    String userViewType,
    String name,
    String email
) {
}
