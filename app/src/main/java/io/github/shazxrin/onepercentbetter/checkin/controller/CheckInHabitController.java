package io.github.shazxrin.onepercentbetter.checkin.controller;

import io.github.shazxrin.onepercentbetter.checkin.dto.CheckInHabitAddRequest;
import io.github.shazxrin.onepercentbetter.checkin.dto.CheckInHabitUpdateRequest;
import io.github.shazxrin.onepercentbetter.checkin.service.CheckInHabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Check In Habits",
    description = "API for managing check ins for habits"
)
@RequestMapping("/api/check-ins/habits")
@RestController
public class CheckInHabitController {
    private final CheckInHabitService checkInHabitService;

    public CheckInHabitController(CheckInHabitService checkInHabitService) {
        this.checkInHabitService = checkInHabitService;
    }

    @Operation(summary = "Check in habit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Check in habit successfully")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{habitId}")
    public void postAddCheckInHabit(
        @PathVariable long habitId,
        @RequestBody CheckInHabitAddRequest checkInHabitAddRequest
    ) {
        checkInHabitService.addCheckIn(
            habitId,
            checkInHabitAddRequest.date(),
            checkInHabitAddRequest.amount(),
            checkInHabitAddRequest.notes()
        );
    }

    @Operation(summary = "Remove check in habit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Removed check in habit successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{checkInId}")
    public void deleteRemoveCheckInHabit(
        @PathVariable long checkInId
    ) {
        checkInHabitService.removeCheckIn(checkInId);
    }

    @Operation(summary = "Update check in habit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated check in habit successfully")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{checkInId}")
    public void putUpdateCheckInHabit(
        @PathVariable long checkInId,
        @RequestBody CheckInHabitUpdateRequest checkInHabitUpdateRequest
    ) {
        checkInHabitService.updateCheckIn(
            checkInId,
            checkInHabitUpdateRequest.date(),
            checkInHabitUpdateRequest.amount(),
            checkInHabitUpdateRequest.notes()
        );
    }
}
