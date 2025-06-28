package io.github.shazxrin.onepercentbetter.github.dto.commit;

import java.net.URI;

public record CommitTree(
    String sha,
    URI url
) {
}
