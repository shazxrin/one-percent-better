package io.github.shazxrin.onepercentbetter.checkin.event;

import java.time.LocalDate;
import org.springframework.context.ApplicationEvent;

public class CheckInProjectAddedEvent extends ApplicationEvent {
    private final long projectId;
    private final LocalDate date;

    public CheckInProjectAddedEvent(Object source, long projectId, LocalDate date) {
        super(source);
        this.projectId = projectId;
        this.date = date;
    }

    public long getProjectId() {
        return projectId;
    }

    public LocalDate getDate() {
        return date;
    }
}
