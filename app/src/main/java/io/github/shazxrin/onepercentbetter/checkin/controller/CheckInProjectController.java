package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Check Ins",
    description = "API for managing check ins"
)
@RequestMapping("/api/check-ins/projects")
@RestController
public class CheckInProjectController {
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectController(CheckInProjectService checkInProjectService) {
        this.checkInProjectService = checkInProjectService;
    }

    @Operation(summary = "Check in for all projects on date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check in for all projects on date successfully"),
    })
    @PostMapping("/all")
    public void postCheckInProjectAll(
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        checkInProjectService.checkInAll(date);
    }

    @Operation(summary = "Check in for a projects on date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check in for project on date successfully"),
    })
    @PostMapping("/{projectId:^[0-9]*}")
    public void postCheckInProject(
        @PathVariable long projectId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        checkInProjectService.checkIn(projectId, date);
    }

    @Operation(summary = "Check in for all projects between date interval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check in for all projects between date interval successfully"),
    })
    @PostMapping("/interval/all")
    public void postCheckInProjectAllInterval(
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        checkInProjectService.checkInAllInterval(fromDate, toDate);
    }

    @Operation(summary = "Check in for a projects between date interval")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check in for project between date interval successfully"),
    })
    @PostMapping("/interval/{projectId:^[0-9]*}")
    public void postCheckInProjectInterval(
        @PathVariable long projectId,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        checkInProjectService.checkInInterval(projectId, fromDate, toDate);
    }
}
