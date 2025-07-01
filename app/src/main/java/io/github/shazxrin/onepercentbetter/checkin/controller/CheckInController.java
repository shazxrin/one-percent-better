package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.service.CheckInService;
import io.github.shazxrin.onepercentbetter.checkin.dto.TodaysCheckIn;
import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Check Ins",
    description = "API for managing check ins"
)
@RequestMapping("/api/check-ins")
@RestController
public class CheckInController {
    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @Operation(summary = "Check in today")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check in today successfully"),
    })
    @PostMapping("/today")
    public void postCheckInToday() {
        checkInService.checkInToday();
    }

    @Operation(summary = "Get info about today's check in")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get info about today's check in successfully"),
    })
    @GetMapping("/today")
    public TodaysCheckIn getCheckInToday() {
        CheckIn todaysCheckIn = checkInService.getTodaysCheckIn();

        return TodaysCheckIn.fromCheckIn(todaysCheckIn);
    }
}
