package io.github.shazxrin.onepercentbetter.github.model;

import java.net.URI;

public record CommitParent(
    String sha,
    URI url,
    URI htmlUrl
) {
}
