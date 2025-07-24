package io.github.shazxrin.onepercentbetter.github.model;

import java.net.URI;

public record CommitDiffEntry(
    String sha,
    String filename,
    String status,
    int additions,
    int deletions,
    int changes,
    URI blobUrl,
    URI rawUrl,
    URI contentsUrl,
    String patch,
    String previousFilename
) {
}
