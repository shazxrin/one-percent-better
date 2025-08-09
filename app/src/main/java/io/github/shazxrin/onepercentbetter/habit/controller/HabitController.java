package io.github.shazxrin.onepercentbetter.habit.controller;

import io.github.shazxrin.onepercentbetter.habit.dto.AddHabitRequest;
import io.github.shazxrin.onepercentbetter.habit.dto.GetAllHabitsResponse;
import io.github.shazxrin.onepercentbetter.habit.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Habits",
    description = "API for managing habits"
)
@RequestMapping("/api/habits")
@RestController
public class HabitController {
    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @Operation(summary = "Get all habits")
    @ApiResponse(responseCode = "200", description = "Get all habits successfully")
    @GetMapping
    public GetAllHabitsResponse getAllHabits() {
        GetAllHabitsResponse getAllHabitsResponse = new GetAllHabitsResponse();
        habitService.getAllHabits()
            .forEach(habit -> getAllHabitsResponse.add(
                new GetAllHabitsResponse.ListItem(habit.getId(), habit.getName()))
            );
        return getAllHabitsResponse;
    }

    @Operation(summary = "Add a new habit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Add a new habit successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void postAddHabit(@RequestBody AddHabitRequest addHabitRequest) {
        habitService.addHabit(addHabitRequest.name(), addHabitRequest.description());
    }

    @Operation(summary = "Delete a habit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delete a habit successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Habit not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteRemoveHabit(@PathVariable long id) {
        habitService.removeHabit(id);
    }
}
