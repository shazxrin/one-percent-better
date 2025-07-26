package io.github.shazxrin.onepercentbetter.github.model;

import java.net.URI;
import java.util.List;

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

