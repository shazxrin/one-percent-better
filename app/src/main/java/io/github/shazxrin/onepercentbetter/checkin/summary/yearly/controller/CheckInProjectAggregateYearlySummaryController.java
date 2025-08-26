package io.github.shazxrin.onepercentbetter.checkin.summary.yearly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.dto.CheckInProjectAggregateYearlySummaryResponse;
import io.github.shazxrin.onepercentbetter.checkin.summary.yearly.service.CheckInProjectAggregateYearlySummaryService;
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
    name = "Check In Project Aggregate Yearly Summaries",
    description = "API for managing check in project aggregate yearly summaries."
)
@RequestMapping("/api/check-ins/projects/yearly-summaries/aggregate")
@RestController
public class CheckInProjectAggregateYearlySummaryController {
    private final CheckInProjectAggregateYearlySummaryService checkInProjectAggregateYearlySummaryService;

    public CheckInProjectAggregateYearlySummaryController(CheckInProjectAggregateYearlySummaryService checkInProjectAggregateYearlySummaryService) {
        this.checkInProjectAggregateYearlySummaryService = checkInProjectAggregateYearlySummaryService;
    }

    @Operation(summary = "Get check-in project aggregate yearly summary for year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get check-in project aggregate yearly summary for year successfully")
    })
    @GetMapping("/{year}")
    public CheckInProjectAggregateYearlySummaryResponse getCheckInProjectAggregateYearlySummary(
        @PathVariable int year
    ) {
        var summary = checkInProjectAggregateYearlySummaryService.getAggregateSummary(year);
        return CheckInProjectAggregateYearlySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate check-in project aggregate yearly summary for year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculate check-in project aggregate yearly summary for year successfully")
    })
    @PostMapping("/{year}")
    public void postCalculateCheckInProjectAggregateYearlySummary(
        @PathVariable int year
    ) {
        checkInProjectAggregateYearlySummaryService.calculateAggregateSummaryForYear(year);
    }
}