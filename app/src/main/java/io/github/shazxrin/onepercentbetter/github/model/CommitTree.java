package io.github.shazxrin.onepercentbetter.github.model;

import java.net.URI;

public record CommitTree(
    String sha,
    URI url
) {
}
