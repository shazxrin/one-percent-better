package io.github.shazxrin.onepercentbetter.github.dto.commit;

public record CommitStats(
    Integer additions,
    Integer deletions,
    Integer total
) {
}
