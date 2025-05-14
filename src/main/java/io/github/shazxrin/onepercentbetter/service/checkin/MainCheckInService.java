package io.github.shazxrin.onepercentbetter.service.checkin;

import io.github.shazxrin.onepercentbetter.model.CheckIn;
import io.github.shazxrin.onepercentbetter.model.Project;
import io.github.shazxrin.onepercentbetter.repository.CheckInRepository;
import io.github.shazxrin.onepercentbetter.service.github.GitHubService;
import io.github.shazxrin.onepercentbetter.service.project.ProjectService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MainCheckInService implements CheckInService {
    private static final Logger log = LoggerFactory.getLogger(MainCheckInService.class);

    private final CheckInRepository checkInRepository;

    private final ProjectService projectService;
    private final GitHubService gitHubService;

    public MainCheckInService(
        CheckInRepository checkInRepository,
        ProjectService projectService,
        GitHubService gitHubService
    ) {
        this.checkInRepository = checkInRepository;
        this.projectService = projectService;
        this.gitHubService = gitHubService;
    }

    private record CountStreak(int count, int streak) {
    }

    private CountStreak calculateTodaysCountStreak() {
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

        return new CountStreak(count, streak);
    }

    @Override
    public void checkInToday() {
        CountStreak todaysCountStreak = calculateTodaysCountStreak();

        CheckIn todaysCheckIn = checkInRepository.findByDate(LocalDate.now());

        if (todaysCheckIn == null) {
            checkInRepository.save(
                new CheckIn(
                    null,
                    LocalDate.now(),
                    todaysCountStreak.count(),
                    todaysCountStreak.streak()
                )
            );
        } else {
            todaysCheckIn.setCount(todaysCountStreak.count());
            todaysCheckIn.setStreak(todaysCountStreak.streak());
            checkInRepository.save(todaysCheckIn);
        }
    }

    @Override
    public CheckIn getTodaysCheckIn() {
        CheckIn todaysCheckIn = checkInRepository.findByDate(LocalDate.now());

        if (todaysCheckIn == null) {
            CountStreak todaysCountStreak = calculateTodaysCountStreak();
            todaysCheckIn = checkInRepository.save(
                new CheckIn(
                    null,
                    LocalDate.now(),
                    todaysCountStreak.count(),
                    todaysCountStreak.streak()
                )
            );

        }

        return todaysCheckIn;
    }
}
