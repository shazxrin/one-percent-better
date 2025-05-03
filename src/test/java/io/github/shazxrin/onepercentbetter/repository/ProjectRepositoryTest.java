package io.github.shazxrin.onepercentbetter.repository;

import io.github.shazxrin.onepercentbetter.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testExistsByOwnerAndName_whenProjectExists_shouldReturnTrue() {
        // Given
        Project project = new Project(null, "github-user", "awesome-project");
        entityManager.persistAndFlush(project);

        // When
        boolean exists = projectRepository.existsByOwnerAndName("github-user", "awesome-project");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByOwnerAndName_whenProjectDoesNotExist_shouldReturnFalse() {
        // Given
        // No project with this owner and name

        // When
        boolean exists = projectRepository.existsByOwnerAndName("nonexistent-user", "nonexistent-repo");

        // Then
        assertFalse(exists);
    }

    @Test
    void testDeleteByOwnerAndName_whenProjectExists_shouldRemoveProject() {
        // Given
        Project project = new Project(null, "delete-owner", "delete-repo");
        entityManager.persistAndFlush(project);
        assertTrue(projectRepository.existsByOwnerAndName("delete-owner", "delete-repo"));

        // When
        projectRepository.deleteByOwnerAndName("delete-owner", "delete-repo");
        entityManager.flush();

        // Then
        boolean exists = projectRepository.existsByOwnerAndName("delete-owner", "delete-repo");
        assertFalse(exists);
    }

    @Test
    void testDeleteByOwnerAndName_whenProjectDoesNotExist_shouldNotFail() {
        // Given
        // No project with this owner and name

        // When & Then (no exception should be thrown)
        projectRepository.deleteByOwnerAndName("nonexistent-user", "nonexistent-repo");
        entityManager.flush();

        // Extra verification
        boolean exists = projectRepository.existsByOwnerAndName("nonexistent-user", "nonexistent-repo");
        assertFalse(exists);
    }
}
