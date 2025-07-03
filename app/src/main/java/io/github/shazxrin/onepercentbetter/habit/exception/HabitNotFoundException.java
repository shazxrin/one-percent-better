package io.github.shazxrin.onepercentbetter.habit.exception;

public class HabitNotFoundException extends RuntimeException {
    public HabitNotFoundException(String message) {
        super(message);
    }
}
