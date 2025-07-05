package io.github.shazxrin.onepercentbetter.utils.project;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectUtil {
    private static final String ALPHANUMERIC_OWNER_NAME_REGEX = "^[a-zA-Z0-9]+/[a-zA-Z0-9\\-_]+$";
    private static final Pattern PATTERN = Pattern.compile(ALPHANUMERIC_OWNER_NAME_REGEX);

    public static boolean isProjectNameValid(String name) {
        if (name == null) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(name);
        return matcher.matches();
    }

    public static ProjectOwnerName parseProjectRepoOwnerName(String name) {
        if (!isProjectNameValid(name)) {
            throw new IllegalArgumentException("Invalid project name: " + name);
        }
        String[] parts = name.split("/");
        return new ProjectOwnerName(parts[0], parts[1]);
    }
}
