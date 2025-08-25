package io.github.shazxrin.onepercentbetter.checkin.summary.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.dto.CheckInProjectWeeklySummaryResponse;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectWeeklySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "CheckInProjectWeeklySummaryController",
    description = "API for managing weekly summaries of check-ins for projects."
)
@RequestMapping("/api/check-ins/projects/weekly-summaries")
@RestController
public class CheckInProjectWeeklySummaryController {
    private final CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService;

    public CheckInProjectWeeklySummaryController(CheckInProjectWeeklySummaryService checkInProjectWeeklySummaryService) {
        this.checkInProjectWeeklySummaryService = checkInProjectWeeklySummaryService;
    }

    @Operation(summary = "Get weekly summary of check-ins for a project.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get weekly summary of check-ins for a project successfully"),
    })
    @GetMapping("/{projectId}/{year}/{weekNo}")
    public CheckInProjectWeeklySummaryResponse getCheckInProjectWeeklySummary(
        @PathVariable long projectId,
        @PathVariable int year,
        @PathVariable int weekNo
    ) {
        var summary = checkInProjectWeeklySummaryService.getSummary(projectId, year, weekNo);
        return CheckInProjectWeeklySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate weekly summary of check-ins for a project.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculate weekly summary of check-ins for a project successfully"),
    })
    @PostMapping("/{projectId}/{year}/{weekNo}")
    public void postCalculatesCheckInProjectWeeklySummary(
        @PathVariable long projectId,
        @PathVariable int year,
        @PathVariable int weekNo
    ) {
        checkInProjectWeeklySummaryService.calculateSummaryForWeek(projectId, year, weekNo);
    }
}
