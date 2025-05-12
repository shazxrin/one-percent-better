package io.github.shazxrin.onepercentbetter.controller;

import io.github.shazxrin.onepercentbetter.service.checkin.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @Operation(summary = "Check in today")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check in today successfully"),
    })
    @PostMapping("/today")
    public void checkInToday() {
        checkInService.checkInToday();
    }
}
