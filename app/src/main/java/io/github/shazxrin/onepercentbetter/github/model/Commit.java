package io.github.shazxrin.onepercentbetter.github.model;

import java.util.List;
import java.net.URI;

public record Commit(
    URI url,
    String sha,
    String nodeId,
    URI htmlUrl,
    URI commentsUrl,
    CommitDetail commit,
    CommitGitHubUser author,
    CommitGitHubUser committer,
    List<CommitParent> parents,
    CommitStats stats,
    List<CommitDiffEntry> files
) {
}

