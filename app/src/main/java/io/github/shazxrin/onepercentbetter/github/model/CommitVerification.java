package io.github.shazxrin.onepercentbetter.github.model;

import java.time.OffsetDateTime;

public record CommitVerification(
    boolean verified,
    String reason,
    String payload,
    String signature,
    OffsetDateTime verifiedAt
) {
}
