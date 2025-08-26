package io.github.shazxrin.onepercentbetter.checkin.summary.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.dto.CheckInProjectAggregateWeeklySummaryResponse;
import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectAggregateWeeklySummaryService;
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
    name = "Check In Project Aggregate Weekly Summaries",
    description = "API for managing check in project aggregate weekly summaries."
)
@RequestMapping("/api/check-ins/projects/weekly-summaries/aggregate")
@RestController
public class CheckInProjectAggregateWeeklySummaryController {
    private final CheckInProjectAggregateWeeklySummaryService checkInProjectAggregateWeeklySummaryService;

    public CheckInProjectAggregateWeeklySummaryController(CheckInProjectAggregateWeeklySummaryService checkInProjectAggregateWeeklySummaryService) {
        this.checkInProjectAggregateWeeklySummaryService = checkInProjectAggregateWeeklySummaryService;
    }

    @Operation(summary = "Get check-in project aggregate weekly summary for year and week")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get check-in project aggregate weekly summary for year and week successfully")
    })
    @GetMapping("/{year}/{weekNo}")
    public CheckInProjectAggregateWeeklySummaryResponse getCheckInProjectAggregateWeeklySummary(
        @PathVariable int year,
        @PathVariable int weekNo
    ) {
        var summary = checkInProjectAggregateWeeklySummaryService.getAggregateSummary(year, weekNo);
        return CheckInProjectAggregateWeeklySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate check-in project aggregate weekly summary for year and week")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculate check-in project aggregate weekly summary for year and week successfully")
    })
    @PostMapping("/{year}/{weekNo}")
    public void postCalculatesCheckInProjectAggregateWeeklySummary(
        @PathVariable int year,
        @PathVariable int weekNo
    ) {
        checkInProjectAggregateWeeklySummaryService.calculateAggregateSummaryForWeek(year, weekNo);
    }
}
