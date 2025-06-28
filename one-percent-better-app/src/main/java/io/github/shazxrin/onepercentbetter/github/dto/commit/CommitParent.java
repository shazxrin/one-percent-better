package io.github.shazxrin.onepercentbetter.github.dto.commit;

import java.net.URI;

public record CommitParent(
    String sha,
    URI url,
    URI htmlUrl
) {
}
