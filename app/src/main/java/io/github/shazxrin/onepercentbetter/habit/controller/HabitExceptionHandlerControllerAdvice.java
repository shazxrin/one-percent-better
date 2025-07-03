package io.github.shazxrin.onepercentbetter.habit.controller;

import io.github.shazxrin.onepercentbetter.habit.exception.HabitNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HabitExceptionHandlerControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HabitNotFoundException.class)
    public ProblemDetail handleHabitNotFoundException(HabitNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
