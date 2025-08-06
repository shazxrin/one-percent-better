package io.github.shazxrin.onepercentbetter.project.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException() {
        super("Project not found.");
    }
}
