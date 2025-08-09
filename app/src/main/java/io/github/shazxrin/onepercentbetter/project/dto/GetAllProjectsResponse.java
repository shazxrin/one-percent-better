package io.github.shazxrin.onepercentbetter.project.dto;

import java.util.ArrayList;

public final class GetAllProjectsResponse extends ArrayList<GetAllProjectsResponse.ListItem> {
    public record ListItem(long id, String name) {
    }
}
