package io.github.shazxrin.onepercentbetter.checkin.summary.controller;

import io.github.shazxrin.onepercentbetter.checkin.summary.service.CheckInProjectDailySummaryService;
import io.github.shazxrin.onepercentbetter.checkin.summary.dto.CheckInProjectDailySummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Check In Project Daily Summaries",
    description = "API for managing check in project daily summaries"
)
@RequestMapping("/api/check-ins/projects/daily-summaries")
@RestController
public class CheckInProjectDailySummaryController {
    private final CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    public CheckInProjectDailySummaryController(CheckInProjectDailySummaryService checkInProjectDailySummaryService) {
        this.checkInProjectDailySummaryService = checkInProjectDailySummaryService;
    }

    @Operation(summary = "Get check-in project daily summary for date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get check-in project summary for date successfully"),
    })
    @GetMapping("/{projectId}/{date}")
    public CheckInProjectDailySummaryResponse getCheckInProjectDailySummary(
        @PathVariable long projectId,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        var summary = checkInProjectDailySummaryService.getSummary(projectId, date);
        return CheckInProjectDailySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate check-in project daily summary for date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculated check-in project summary for date successfully"),
    })
    @PostMapping("/{projectId}/{date}")
    public void postCalculatesCheckInProjectDailySummary(
        @PathVariable long projectId,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        checkInProjectDailySummaryService.calculateSummaryForDate(projectId, date, true);
    }
}
