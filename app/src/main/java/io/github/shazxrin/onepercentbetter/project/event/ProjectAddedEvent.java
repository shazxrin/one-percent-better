package io.github.shazxrin.onepercentbetter.project.event;

import org.springframework.context.ApplicationEvent;

public class ProjectAddedEvent extends ApplicationEvent {
    private final long projectId;

    public ProjectAddedEvent(Object eventTriggerSource, long projectId) {
        super(eventTriggerSource);
        this.projectId = projectId;
    }

    public long getProjectId() {
        return projectId;
    }
}