package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.dto.CheckInProjectYearlySummaryResponse;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectYearlySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Check In Project Yearly Summaries",
    description = "API for managing yearly summaries of check-ins for projects."
)
@RequestMapping("/api/check-ins/projects/yearly-summaries")
@RestController
public class CheckInProjectYearlySummaryController {
    private final CheckInProjectYearlySummaryService checkInProjectYearlySummaryService;

    public CheckInProjectYearlySummaryController(CheckInProjectYearlySummaryService checkInProjectYearlySummaryService) {
        this.checkInProjectYearlySummaryService = checkInProjectYearlySummaryService;
    }

    @Operation(summary = "Get yearly summary of check-ins for a project.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get yearly summary of check-ins for a project successfully"),
    })
    @GetMapping("/{projectId}/{year}")
    public CheckInProjectYearlySummaryResponse getCheckInProjectYearlySummary(
        @PathVariable long projectId,
        @PathVariable int year
    ) {
        var summary = checkInProjectYearlySummaryService.getSummary(projectId, year);
        return CheckInProjectYearlySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate yearly summary of check-ins for a project.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculate yearly summary of check-ins for a project successfully"),
    })
    @PostMapping("/{projectId}/{year}")
    public void postCalculatesCheckInProjectYearlySummary(
        @PathVariable long projectId,
        @PathVariable int year
    ) {
        checkInProjectYearlySummaryService.calculateSummaryForYear(projectId, year);
    }
}