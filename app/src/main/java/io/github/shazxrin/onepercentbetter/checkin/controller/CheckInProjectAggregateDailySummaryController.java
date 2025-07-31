package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.dto.CheckInProjectAggregateDailySummaryResponse;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectAggregateDailySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Check In Project Aggregate Daily Summaries",
    description = "API for managing check in project aggregate daily summaries"
)
@RequestMapping("/api/check-ins/projects/daily-summaries/aggregate")
@RestController
public class CheckInProjectAggregateDailySummaryController {
    private final CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService;

    public CheckInProjectAggregateDailySummaryController(
        CheckInProjectAggregateDailySummaryService checkInProjectAggregateDailySummaryService
    ) {
        this.checkInProjectAggregateDailySummaryService = checkInProjectAggregateDailySummaryService;
    }

    @Operation(summary = "Get check-in project aggregate daily summary for date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get check-in project aggregate daily summary for date successfully")
    })
    @GetMapping("/{date}")
    public CheckInProjectAggregateDailySummaryResponse getCheckInProjectAggregateDailySummary(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        var summary = checkInProjectAggregateDailySummaryService.getAggregateSummary(date);
        return CheckInProjectAggregateDailySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate check-in project aggregate daily summary for date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculated check-in project aggregate daily summary for date successfully")
    })
    @GetMapping("/{date}")
    public void postCalculatesCheckInProjectAggregateDailySummary(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        checkInProjectAggregateDailySummaryService.calculateAggregateSummary(date);
    }
}
