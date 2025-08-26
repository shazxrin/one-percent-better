package io.github.shazxrin.onepercentbetter.utility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProjectNameUtilityTest {
    @Test
    void testIsProjectNameValid_validNames() {
        assertTrue(ProjectNameUtility.isProjectNameValid("owner/repo"));
        assertTrue(ProjectNameUtility.isProjectNameValid("owner123/repo-name_with_underscore"));
        assertTrue(ProjectNameUtility.isProjectNameValid("MyOrg/MyRepo"));
        assertTrue(ProjectNameUtility.isProjectNameValid("a/b"));
    }

    @Test
    void testIsProjectNameValid_invalidNames() {
        assertFalse(ProjectNameUtility.isProjectNameValid(null));
        assertFalse(ProjectNameUtility.isProjectNameValid(""));
        assertFalse(ProjectNameUtility.isProjectNameValid("owner"));
        assertFalse(ProjectNameUtility.isProjectNameValid("owner/"));
        assertFalse(ProjectNameUtility.isProjectNameValid("/repo"));
        assertFalse(ProjectNameUtility.isProjectNameValid("owner/repo/sub"));
        assertFalse(ProjectNameUtility.isProjectNameValid("owner/repo.name"));
        assertFalse(ProjectNameUtility.isProjectNameValid("owner /repo"));
        assertFalse(ProjectNameUtility.isProjectNameValid("owner/repo "));
    }

    @Test
    void testParseProjectRepoOwnerName_validName() {
        ProjectOwnerName projectOwnerName = ProjectNameUtility.parseProjectRepoOwnerName("shazxrin/onepercentbetter");
        assertNotNull(projectOwnerName);
        assertEquals("shazxrin", projectOwnerName.owner());
        assertEquals("onepercentbetter", projectOwnerName.name());

        projectOwnerName = ProjectNameUtility.parseProjectRepoOwnerName("org123/my-project_name");
        assertNotNull(projectOwnerName);
        assertEquals("org123", projectOwnerName.owner());
        assertEquals("my-project_name", projectOwnerName.name());
    }

    @Test
    void testParseProjectRepoOwnerName_invalidNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ProjectNameUtility.parseProjectRepoOwnerName(null));
        assertThrows(IllegalArgumentException.class, () -> ProjectNameUtility.parseProjectRepoOwnerName(""));
        assertThrows(IllegalArgumentException.class, () -> ProjectNameUtility.parseProjectRepoOwnerName("invalid name"));
        assertThrows(IllegalArgumentException.class, () -> ProjectNameUtility.parseProjectRepoOwnerName("owneronly"));
    }
}