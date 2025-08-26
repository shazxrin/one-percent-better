package io.github.shazxrin.onepercentbetter.checkin.summary.utility;

import io.github.shazxrin.onepercentbetter.checkin.core.model.CheckInProject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class StreakUtility {
    public static int calculateMaxStreakFromDayDistribution(Map<String, Integer> dayDistribution) {
        int maxStreak = 0;
        int currentStreak = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            String dayName = day.toString();
            if (dayDistribution.getOrDefault(dayName, 0) > 0) {
                currentStreak++;
            } else {
                currentStreak = 0;
            }
            maxStreak = Math.max(maxStreak, currentStreak);
        }
        return maxStreak;
    }

    public static int calculateMaxStreakFromDateDistribution(Map<String, Integer> dateDistribution) {
        int maxStreak = 0;
        int currentStreak = 0;
        for (int i = 1; i <= dateDistribution.size(); i++) {
            String date = String.valueOf(i);
            if (dateDistribution.get(date) > 0) {
                currentStreak++;
            } else {
                currentStreak = 0;
            }
            maxStreak = Math.max(maxStreak, currentStreak);
        }
        return maxStreak;
    }

    public static int calculateMaxStreakFromCheckIns(List<CheckInProject> checkInProjects) {
        List<LocalDate> dates = checkInProjects.stream()
            .map(c -> c.getDateTime().toLocalDate())
            .distinct()
            .sorted()
            .toList();

        if (dates.isEmpty()) {
            return 0;
        }

        int maxStreak = 0;
        int currentStreak = 0;
        for (int i = 0; i < dates.size(); i++) {
            if (i == 0) {
                currentStreak = 1;
            } else {
                LocalDate previousDate = dates.get(i - 1);
                LocalDate currentDate = dates.get(i);

                if (currentDate.minusDays(1).isEqual(previousDate)) {
                    currentStreak++;
                } else {
                    currentStreak = 1;
                }
            }
            maxStreak = Math.max(maxStreak, currentStreak);
        }

        return maxStreak;
    }
}
