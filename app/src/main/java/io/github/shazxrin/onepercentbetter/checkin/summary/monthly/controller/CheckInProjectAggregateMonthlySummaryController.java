package io.github.shazxrin.onepercentbetter.checkin.summary.monthly.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.dto.CheckInProjectAggregateMonthlySummaryResponse;
import io.github.shazxrin.onepercentbetter.checkin.summary.monthly.service.CheckInProjectAggregateMonthlySummaryService;
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
    name = "Check In Project Aggregate Monthly Summaries",
    description = "API for managing check in project aggregate monthly summaries."
)
@RequestMapping("/api/check-ins/projects/monthly-summaries/aggregate")
@RestController
public class CheckInProjectAggregateMonthlySummaryController {
    private final CheckInProjectAggregateMonthlySummaryService checkInProjectAggregateMonthlySummaryService;

    public CheckInProjectAggregateMonthlySummaryController(CheckInProjectAggregateMonthlySummaryService checkInProjectAggregateMonthlySummaryService) {
        this.checkInProjectAggregateMonthlySummaryService = checkInProjectAggregateMonthlySummaryService;
    }

    @Operation(summary = "Get check-in project aggregate monthly summary for year and month")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get check-in project aggregate monthly summary for year and month successfully")
    })
    @GetMapping("/{year}/{monthNo}")
    public CheckInProjectAggregateMonthlySummaryResponse getCheckInProjectAggregateMonthlySummary(
        @PathVariable int year,
        @PathVariable int monthNo
    ) {
        var summary = checkInProjectAggregateMonthlySummaryService.getAggregateSummary(year, monthNo);
        return CheckInProjectAggregateMonthlySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate check-in project aggregate monthly summary for year and month")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculate check-in project aggregate monthly summary for year and month successfully")
    })
    @PostMapping("/{year}/{monthNo}")
    public void postCalculateCheckInProjectAggregateMonthlySummary(
        @PathVariable int year,
        @PathVariable int monthNo
    ) {
        checkInProjectAggregateMonthlySummaryService.calculateAggregateSummaryForMonth(year, monthNo);
    }
}