package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.dto.CheckInProjectDailySummaryResponse;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInProjectDailySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(
    name = "Check Ins",
    description = "API for managing check ins"
)
@RequestMapping("/api/check-in/projects/daily-summaries")
@RestController
public class CheckInProjectDailySummaryController {
    private final CheckInProjectDailySummaryService checkInProjectDailySummaryService;

    public CheckInProjectDailySummaryController(CheckInProjectDailySummaryService checkInProjectDailySummaryService) {
        this.checkInProjectDailySummaryService = checkInProjectDailySummaryService;
    }

    @Operation(summary = "Get check-in project summary for date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get check-in project summary for date successfully"),
    })
    @GetMapping("/{projectId}/{date}")
    public CheckInProjectDailySummaryResponse getCheckInProjectDailySummary(
        @PathVariable long projectId,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        var summary = checkInProjectDailySummaryService.getDailySummary(projectId, date);
        return CheckInProjectDailySummaryResponse.from(summary);
    }

    @Operation(summary = "Calculate check-in project summary for date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated check-in project summary for date successfully"),
    })
    @PostMapping("/{projectId}/{date}")
    public void postCalculatesCheckInProjectDailySummary(
        @PathVariable long projectId,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        checkInProjectDailySummaryService.calculateCheckInDailySummary(projectId, date);
    }
}
