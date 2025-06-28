package io.github.shazxrin.onepercentbetter.github.dto.commit;

import java.time.OffsetDateTime;

public record CommitGitUser(
    String name,
    String email,
    OffsetDateTime date
) {
}
