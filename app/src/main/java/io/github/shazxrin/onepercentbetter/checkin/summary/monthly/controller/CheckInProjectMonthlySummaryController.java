package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.dto.CheckInProjectMonthlySummaryResponse;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service.CheckInProjectMonthlySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Check In Project Monthly Summaries",
    description = "API for managing monthly summaries of check-ins for projects."
)
@RequestMapping("/api/check-ins/projects/monthly-summaries")
@RestController
public class CheckInProjectMonthlySummaryController {
    private final CheckInProjectMonthlySummaryService checkInProjectMonthlySummaryService;

    public CheckInProjectMonthlySummaryController(CheckInProjectMonthlySummaryService checkInProjectMonthlySummaryService) {
        this.checkInProjectMonthlySummaryService = checkInProjectMonthlySummaryService;
    }

    @Operation(summary = "Get monthly summary of check-ins for a project.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get monthly summary of check-ins for a project successfully"),
    })
    @GetMapping("/{projectId}/{year}/{monthNo}")
    public CheckInProjectMonthlySummaryResponse getCheckInProjectMonthlySummary(
        @PathVariable long projectId,
        @PathVariable int year,
        @PathVariable int monthNo
    ) {
        var summary = checkInProjectMonthlySummaryService.getSummary(projectId, year, monthNo);
        return CheckInProjectMonthlySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate monthly summary of check-ins for a project.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculate monthly summary of check-ins for a project successfully"),
    })
    @PostMapping("/{projectId}/{year}/{monthNo}")
    public void postCalculatesCheckInProjectMonthlySummary(
        @PathVariable long projectId,
        @PathVariable int year,
        @PathVariable int monthNo
    ) {
        checkInProjectMonthlySummaryService.calculateSummaryForMonth(projectId, year, monthNo);
    }
}
