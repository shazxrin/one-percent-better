package io.github.shazxrin.onepercentbetter.service;

import io.github.shazxrin.onepercentbetter.model.CheckIn;
import io.github.shazxrin.onepercentbetter.model.Project;
import io.github.shazxrin.onepercentbetter.repository.CheckInRepository;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckInService {
    private static final Logger log = LoggerFactory.getLogger(CheckInService.class);

    private final CheckInRepository checkInRepository;

    private final ProjectService projectService;
    private final GitHubService gitHubService;

    @Autowired
    public CheckInService(
        CheckInRepository checkInRepository,
        ProjectService projectService,
        GitHubService gitHubService
    ) {
        this.checkInRepository = checkInRepository;
        this.projectService = projectService;
        this.gitHubService = gitHubService;
    }

    public void checkInToday() {
        int count = 0;

        Iterable<Project> projects = projectService.getAllProjects();
        for (Project project : projects) {
            log.info("Checking {}/{}", project.getOwner(), project.getName());
            count += gitHubService.getCommitCountTodayForRepository(project.getOwner(), project.getName());
        }
        log.info("Total commit count is {}", count);

        int streak = 0;
        LocalDate yesterday = LocalDate.now().minusDays(1);
        CheckIn yesterdayCheckIn = checkInRepository.findByDate(yesterday);
        if (yesterdayCheckIn == null) {
            if (count > 0) {
                streak = 1;
            }
        } else {
            if (count > 0) {
                streak = yesterdayCheckIn.getStreak() + 1;
            }
        }

        CheckIn todaysCheckIn = checkInRepository.findByDate(LocalDate.now());
        if (todaysCheckIn == null) {
            checkInRepository.save(new CheckIn(null, LocalDate.now(), count, streak));
        } else {
            todaysCheckIn.setCount(count);
            todaysCheckIn.setStreak(streak);
            checkInRepository.save(todaysCheckIn);
        }
    }
}
