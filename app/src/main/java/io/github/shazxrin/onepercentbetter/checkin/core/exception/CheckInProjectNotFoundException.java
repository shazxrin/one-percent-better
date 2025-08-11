package io.github.shazxrin.onepercentbetter.checkin.core.exception;

public class CheckInProjectNotFoundException extends RuntimeException {
    public CheckInProjectNotFoundException() {
        super("Check in project not found.");
    }
}
