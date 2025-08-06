package io.github.shazxrin.onepercentbetter.checkin.core.event;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProjectSource;
import java.time.LocalDate;
import org.springframework.context.ApplicationEvent;

public class CheckInProjectAddedEvent extends ApplicationEvent {
    private final long projectId;
    private final LocalDate date;
    private final CheckInProjectSource checkInProjectSource;

    public CheckInProjectAddedEvent(
        Object eventTriggerSource,
        long projectId,
        LocalDate date,
        CheckInProjectSource checkInProjectSource
    ) {
        super(eventTriggerSource);
        this.projectId = projectId;
        this.date = date;
        this.checkInProjectSource = checkInProjectSource;
    }

    public long getProjectId() {
        return projectId;
    }

    public LocalDate getDate() {
        return date;
    }

    public CheckInProjectSource getCheckInProjectSource() {
        return checkInProjectSource;
    }
}
