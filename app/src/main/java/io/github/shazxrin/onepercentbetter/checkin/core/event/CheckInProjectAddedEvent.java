package io.github.shazxrin.onepercentbetter.checkin.core.event;

import java.time.LocalDate;
import org.springframework.context.ApplicationEvent;

public class CheckInProjectAddedEvent extends ApplicationEvent {
    private final long projectId;
    private final long checkInProjectId;
    private final LocalDate date;

    public CheckInProjectAddedEvent(
        Object eventTriggerSource,
        long projectId,
        long checkInProjectId,
        LocalDate date
    ) {
        super(eventTriggerSource);
        this.projectId = projectId;
        this.checkInProjectId = checkInProjectId;
        this.date = date;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getCheckInProjectId() {
        return checkInProjectId;
    }

    public LocalDate getDate() {
        return date;
    }
}
