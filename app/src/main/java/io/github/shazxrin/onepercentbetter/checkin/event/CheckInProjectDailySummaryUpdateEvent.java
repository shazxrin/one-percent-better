package io.github.shazxrin.onepercentbetter.checkin.event;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectDailySummaryService;
import java.time.LocalDate;
import org.springframework.context.ApplicationEvent;

public class CheckInProjectDailySummaryUpdateEvent extends ApplicationEvent {
    private final LocalDate date;

    public CheckInProjectDailySummaryUpdateEvent(Object source, LocalDate date) {
        super(source);
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}
