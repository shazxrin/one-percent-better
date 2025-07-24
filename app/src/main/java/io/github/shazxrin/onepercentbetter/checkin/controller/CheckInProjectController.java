package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Check Ins",
    description = "API for managing check ins"
)
@RequestMapping("/api/check-in/projects")
@RestController
public class CheckInProjectController {
    private final CheckInProjectService checkInProjectService;

    public CheckInProjectController(CheckInProjectService checkInProjectService) {
        this.checkInProjectService = checkInProjectService;
    }

    @Operation(summary = "Check in today")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check in today successfully"),
    })
    @PostMapping("/today")
    public void postCheckInToday() {
        checkInProjectService.checkInAll(LocalDate.now());
    }
}
