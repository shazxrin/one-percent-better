package io.github.shazxrin.onepercentbetter.checkin.core.repository;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.configuration.RepositoryTestConfiguration;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(RepositoryTestConfiguration.class)
@DataJpaTest
public class CheckInProjectRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckInProjectRepository checkInProjectRepository;

    @Test
    void testExistsByProjectIdAndHash_whenHasProjectIdAndHash_shouldReturnTrue() {
        Project project = new Project("Test Project");
        entityManager.persistAndFlush(project);

        CheckInProject checkInProject = new CheckInProject();
        checkInProject.setProject(project);
        checkInProject.setHash("abc123");
        checkInProject.setDateTime(LocalDateTime.now());
        entityManager.persistAndFlush(checkInProject);

        boolean exists = checkInProjectRepository.existsByProjectIdAndHash(project.getId(), "abc123");
        assertTrue(exists);
    }

    @Test
    void testExistsByProjectIdAndHash_whenHasProjectIdButNotHash_shouldReturnFalse() {
        Project project = new Project("Test Project");
        entityManager.persistAndFlush(project);

        CheckInProject checkInProject = new CheckInProject();
        checkInProject.setProject(project);
        checkInProject.setHash("abc123");
        checkInProject.setDateTime(LocalDateTime.now());
        entityManager.persistAndFlush(checkInProject);

        boolean exists = checkInProjectRepository.existsByProjectIdAndHash(project.getId(), "wronghash");
        assertFalse(exists);
    }

    @Test
    void testExistsByProjectIdAndHash_whenHasNoProjectIdButHash_shouldReturnFalse() {
        Project project1 = new Project("Project 1");
        entityManager.persistAndFlush(project1);

        Project project2 = new Project("Project 2");
        entityManager.persistAndFlush(project2);

        CheckInProject checkInProject = new CheckInProject();
        checkInProject.setProject(project2);
        checkInProject.setHash("abc123");
        checkInProject.setDateTime(LocalDateTime.now());
        entityManager.persistAndFlush(checkInProject);

        boolean exists = checkInProjectRepository.existsByProjectIdAndHash(project1.getId(), "abc123");
        assertFalse(exists);
    }

    @Test
    void testFindByDateTimeBetween_whenHaveCheckIns_shouldReturnCount() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 1, 12, 0);

        Project project1 = new Project("Project 1");
        project1 = entityManager.persist(project1);

        Project project2 = new Project("Project 2");
        project2 = entityManager.persist(project2);

        CheckInProject checkInProject1 = new CheckInProject();
        checkInProject1.setProject(project1);
        checkInProject1.setHash("hash1");
        checkInProject1.setDateTime(dateTime);
        entityManager.persist(checkInProject1);

        CheckInProject checkInProject2 = new CheckInProject();
        checkInProject2.setProject(project1);
        checkInProject2.setHash("hash2");
        checkInProject2.setDateTime(dateTime);
        entityManager.persist(checkInProject2);

        CheckInProject checkInProject3 = new CheckInProject();
        checkInProject3.setProject(project2);
        checkInProject3.setHash("hash3");
        checkInProject3.setDateTime(dateTime);
        entityManager.persist(checkInProject3);

        entityManager.flush();

        List<CheckInProject> checkInProjects = checkInProjectRepository.findByProjectIdAndDateTimeBetween(project1.getId(), dateTime.with(LocalTime.MIN), dateTime.with(LocalTime.MAX));
        assertEquals(2, checkInProjects.size());
    }

    @Test
    void testFindByProjectIdAndDateTimeBetween_whenHaveCheckIns_shouldReturnCount() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 1, 12, 0);

        Project project = new Project("Project 1");
        project = entityManager.persist(project);

        CheckInProject checkInProject1 = new CheckInProject();
        checkInProject1.setProject(project);
        checkInProject1.setHash("hash1");
        checkInProject1.setDateTime(dateTime);
        entityManager.persist(checkInProject1);

        CheckInProject checkInProject2 = new CheckInProject();
        checkInProject2.setProject(project);
        checkInProject2.setHash("hash2");
        checkInProject2.setDateTime(dateTime);
        entityManager.persist(checkInProject2);

        CheckInProject checkInProject3 = new CheckInProject();
        checkInProject3.setProject(project);
        checkInProject3.setHash("hash3");
        checkInProject3.setDateTime(LocalDateTime.of(2024, 6, 2, 12, 0));
        entityManager.persist(checkInProject3);

        entityManager.flush();

        List<CheckInProject> checkInProjects = checkInProjectRepository.findByProjectIdAndDateTimeBetween(project.getId(), dateTime.with(LocalTime.MIN), dateTime.with(LocalTime.MAX));
        assertEquals(2, checkInProjects.size());
    }
}