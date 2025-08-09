package io.github.shazxrin.onepercentbetter.habit.dto;

import java.util.ArrayList;

public final class GetAllHabitsResponse extends ArrayList<GetAllHabitsResponse.ListItem> {
    public record ListItem(long id, String name) {
    }
}
