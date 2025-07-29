package io.github.shazxrin.onepercentbetter.checkin.repository;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckInProject;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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
        checkInProject.setDate(LocalDate.now());
        entityManager.persistAndFlush(checkInProject);

        boolean exists = checkInProjectRepository.existsByProjectIdAndHash(project.getId(), "abc123");
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByProjectIdAndHash_whenHasProjectIdButNotHash_shouldReturnFalse() {
        Project project = new Project("Test Project");
        entityManager.persistAndFlush(project);

        CheckInProject checkInProject = new CheckInProject();
        checkInProject.setProject(project);
        checkInProject.setHash("abc123");
        checkInProject.setDate(LocalDate.now());
        entityManager.persistAndFlush(checkInProject);

        boolean exists = checkInProjectRepository.existsByProjectIdAndHash(project.getId(), "wronghash");
        assertThat(exists).isFalse();
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
        checkInProject.setDate(LocalDate.now());
        entityManager.persistAndFlush(checkInProject);

        boolean exists = checkInProjectRepository.existsByProjectIdAndHash(project1.getId(), "abc123");
        assertThat(exists).isFalse();
    }

    @Test
    void testCountByDate_whenHaveByDate_shouldReturnCount() {
        LocalDate date = LocalDate.of(2024, 6, 1);

        Project project1 = new Project("Project 1");
        entityManager.persist(project1);

        Project project2 = new Project("Project 2");
        entityManager.persist(project2);

        Project project3 = new Project("Project 3");
        entityManager.persist(project3);

        CheckInProject checkInProject1 = new CheckInProject();
        checkInProject1.setProject(project1);
        checkInProject1.setHash("hash1");
        checkInProject1.setDate(date);
        entityManager.persist(checkInProject1);

        CheckInProject checkInProject2 = new CheckInProject();
        checkInProject2.setProject(project2);
        checkInProject2.setHash("hash2");
        checkInProject2.setDate(date);
        entityManager.persist(checkInProject2);

        CheckInProject checkInProject3 = new CheckInProject();
        checkInProject3.setProject(project3);
        checkInProject3.setHash("hash3");
        checkInProject3.setDate(LocalDate.of(2024, 6, 2)); // different date
        entityManager.persist(checkInProject3);

        entityManager.flush();

        int count = checkInProjectRepository.countByDate(date);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testCountByDate_whenHaveNoneByDate_shouldReturnZero() {
        LocalDate date = LocalDate.of(2024, 6, 1);

        Project project = new Project("Project 1");
        entityManager.persistAndFlush(project);

        CheckInProject checkInProject = new CheckInProject();
        checkInProject.setProject(project);
        checkInProject.setHash("hash1");
        checkInProject.setDate(LocalDate.of(2024, 6, 2)); // different date
        entityManager.persistAndFlush(checkInProject);

        int count = checkInProjectRepository.countByDate(date);
        assertThat(count).isZero();
    }
}