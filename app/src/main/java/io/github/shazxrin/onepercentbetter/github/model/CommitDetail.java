package io.github.shazxrin.onepercentbetter.github.model;

import java.net.URI;

public record CommitDetail(
    URI url,
    CommitGitUser author,
    CommitGitUser committer,
    String message,
    int commentCount,
    CommitTree tree,
    CommitVerification verification
) {
}
