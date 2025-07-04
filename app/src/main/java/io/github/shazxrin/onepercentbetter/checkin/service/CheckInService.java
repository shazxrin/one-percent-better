package io.github.shazxrin.onepercentbetter.checkin.service;

import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import io.github.shazxrin.onepercentbetter.checkin.repository.CheckInRepository;
import io.github.shazxrin.onepercentbetter.project.model.Project;
import io.github.shazxrin.onepercentbetter.github.service.GitHubService;
import io.github.shazxrin.onepercentbetter.project.service.ProjectService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckInService {
    private static final Logger log = LoggerFactory.getLogger(CheckInService.class);

    private final CheckInRepository checkInRepository;

    private final ProjectService projectService;
    private final GitHubService gitHubService;

    public CheckInService(
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

    private CountStreak calculateCountStreakForDate(LocalDate date) {
        log.info("Calculating count streak for date {}.", date);
        int count = 0;

        Iterable<Project> projects = projectService.getAllProjects();
        for (Project project : projects) {
            log.info("Checking {}/{}.", project.getOwner(), project.getName());
            count += gitHubService.getCommitCountForRepositoryOnDate(project.getOwner(), project.getName(), date);
        }
        log.info("Total commit count is {}.", count);

        int streak = 0;
        LocalDate prevDate = date.minusDays(1);
        CheckIn prevDateCheckIn = checkInRepository.findByDate(prevDate);
        if (prevDateCheckIn == null) {
            if (count > 0) {
                streak = 1;
            }
        } else {
            if (count > 0) {
                streak = prevDateCheckIn.getStreak() + 1;
            }
        }

        return new CountStreak(count, streak);
    }

    private void checkInForDate(LocalDate date) {
        CountStreak todaysCountStreak = calculateCountStreakForDate(date);

        CheckIn checkIn = checkInRepository.findByDate(date);

        if (checkIn == null) {
            checkInRepository.save(
                new CheckIn(
                    date,
                    todaysCountStreak.count(),
                    todaysCountStreak.streak()
                )
            );
        } else {
            checkIn.setCount(todaysCountStreak.count());
            checkIn.setStreak(todaysCountStreak.streak());
            checkInRepository.save(checkIn);
        }
    }

    public void checkInToday() {
        checkInForDate(LocalDate.now());
    }

    public CheckIn getTodaysCheckIn() {
        CheckIn todaysCheckIn = checkInRepository.findByDate(LocalDate.now());

        // Check in for today if not found.
        if (todaysCheckIn == null) {
            CountStreak todaysCountStreak = calculateCountStreakForDate(LocalDate.now());
            todaysCheckIn = checkInRepository.save(
                new CheckIn(
                    LocalDate.now(),
                    todaysCountStreak.count(),
                    todaysCountStreak.streak()
                )
            );
        }

        return todaysCheckIn;
    }

    public void checkInInterval(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before to date.");
        }

        var currentDate = from;
        while (!currentDate.isAfter(to)) {
            checkInForDate(currentDate);
            currentDate = currentDate.plusDays(1);
        }
    }
}
