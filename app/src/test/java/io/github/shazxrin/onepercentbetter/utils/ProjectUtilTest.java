package io.github.shazxrin.onepercentbetter.utils;

import io.github.shazxrin.onepercentbetter.utils.project.ProjectOwnerName;
import io.github.shazxrin.onepercentbetter.utils.project.ProjectUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProjectUtilTest {
    @Test
    void testIsProjectNameValid_validNames() {
        assertTrue(ProjectUtil.isProjectNameValid("owner/repo"));
        assertTrue(ProjectUtil.isProjectNameValid("owner123/repo-name_with_underscore"));
        assertTrue(ProjectUtil.isProjectNameValid("MyOrg/MyRepo"));
        assertTrue(ProjectUtil.isProjectNameValid("a/b"));
    }

    @Test
    void testIsProjectNameValid_invalidNames() {
        assertFalse(ProjectUtil.isProjectNameValid(null));
        assertFalse(ProjectUtil.isProjectNameValid(""));
        assertFalse(ProjectUtil.isProjectNameValid("owner"));
        assertFalse(ProjectUtil.isProjectNameValid("owner/"));
        assertFalse(ProjectUtil.isProjectNameValid("/repo"));
        assertFalse(ProjectUtil.isProjectNameValid("owner/repo/sub"));
        assertFalse(ProjectUtil.isProjectNameValid("owner/repo.name"));
        assertFalse(ProjectUtil.isProjectNameValid("owner /repo"));
        assertFalse(ProjectUtil.isProjectNameValid("owner/repo "));
    }

    @Test
    void testParseProjectRepoOwnerName_validName() {
        ProjectOwnerName projectOwnerName = ProjectUtil.parseProjectRepoOwnerName("shazxrin/onepercentbetter");
        assertNotNull(projectOwnerName);
        assertEquals("shazxrin", projectOwnerName.owner());
        assertEquals("onepercentbetter", projectOwnerName.name());

        projectOwnerName = ProjectUtil.parseProjectRepoOwnerName("org123/my-project_name");
        assertNotNull(projectOwnerName);
        assertEquals("org123", projectOwnerName.owner());
        assertEquals("my-project_name", projectOwnerName.name());
    }

    @Test
    void testParseProjectRepoOwnerName_invalidNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.parseProjectRepoOwnerName(null));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.parseProjectRepoOwnerName(""));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.parseProjectRepoOwnerName("invalid name"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.parseProjectRepoOwnerName("owneronly"));
    }
}