package io.github.shazxrin.onepercentbetter.habit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddHabitRequest(
    @NotNull @NotEmpty @NotBlank String name,
    @NotNull @NotEmpty @NotBlank String description
) {
}