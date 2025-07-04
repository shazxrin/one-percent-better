package io.github.shazxrin.onepercentbetter.project.repository;

import io.github.shazxrin.onepercentbetter.project.model.Project;
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
        Project project = new Project("github-user/awesome-project");
        entityManager.persistAndFlush(project);

        // When
        boolean exists = projectRepository.existsByName("github-user/awesome-project");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByOwnerAndName_whenProjectDoesNotExist_shouldReturnFalse() {
        // Given
        // No project with this owner and name

        // When
        boolean exists = projectRepository.existsByName("nonexistent-user/nonexistent-repo");

        // Then
        assertFalse(exists);
    }
}
