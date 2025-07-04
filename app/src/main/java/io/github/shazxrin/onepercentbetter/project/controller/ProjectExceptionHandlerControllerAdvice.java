package io.github.shazxrin.onepercentbetter.project.controller;

import io.github.shazxrin.onepercentbetter.project.exception.ProjectInvalidFormatException;
import io.github.shazxrin.onepercentbetter.project.exception.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionHandlerControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProjectNotFoundException.class)
    public ProblemDetail handleProjectNotFoundException(ProjectNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProjectInvalidFormatException.class)
    public ProblemDetail handleProjectInvalidNameException(ProjectInvalidFormatException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
