package io.github.shazxrin.onepercentbetter.github.model;

import java.time.OffsetDateTime;

public record CommitGitUser(
    String name,
    String email,
    OffsetDateTime date
) {
}
