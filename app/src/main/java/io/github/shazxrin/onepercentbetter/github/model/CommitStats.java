package io.github.shazxrin.onepercentbetter.github.model;

public record CommitStats(
    Integer additions,
    Integer deletions,
    Integer total
) {
}
