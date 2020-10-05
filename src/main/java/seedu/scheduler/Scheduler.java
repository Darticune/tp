package seedu.scheduler;

import java.time.LocalDate;
import java.lang.Math;

public class Scheduler {
    public static final double EASY_MULTIPLIER = 1.1;
    public static final double MEDIUM_MULTIPLIER = 2.2;
    public static final double HARD_MULTIPLIER = 4.4;

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static int newEasyInterval(int previousInterval) {
        double newInterval = previousInterval * EASY_MULTIPLIER;
        return (int) Math.round(newInterval);
    }

    public static LocalDate setNewEasyDeadline(int previousInterval) {
        int interval = newEasyInterval(previousInterval);
        return getCurrentDate().plusDays(interval);
    }

    public static int newMediumInterval(int previousInterval) {
        double newInterval = previousInterval * MEDIUM_MULTIPLIER;
        return (int) Math.round(newInterval);
    }

    public static LocalDate setNewMediumDeadline(int previousInterval) {
        int interval = newMediumInterval(previousInterval);
        return getCurrentDate().plusDays(interval);
    }

    public static int newHardInterval(int previousInterval) {
        double newInterval = previousInterval * HARD_MULTIPLIER;
        return (int) Math.round(newInterval);
    }

    public static LocalDate setNewHardDeadline(int previousInterval) {
        int interval = newHardInterval(previousInterval);
        return getCurrentDate().plusDays(interval);
    }
}
