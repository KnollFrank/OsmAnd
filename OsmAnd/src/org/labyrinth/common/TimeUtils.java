package org.labyrinth.common;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

    public static long seconds2microseconds(final float seconds) {
        return (long) (seconds * 1000000);
    }

    public static long seconds2milliseconds(final float seconds) {
        return (long) (seconds * 1000);
    }

    public static Duration getSecondsBetween(final Instant instant1, final Instant instant2) {
        return Duration.ofSeconds(ChronoUnit.SECONDS.between(instant1, instant2));
    }

    public static Duration roundToNearestMinute(final Duration duration) {
        return Duration.ofMinutes((duration.getSeconds() + 30) / 60);
    }

    public static String formatHHMM(final Duration duration) {
        final long hoursPart = DurationParts.getHoursPart(duration);
        final long minutesPart = DurationParts.getMinutesPart(duration);
        return hoursPart > 0 ?
                String.format("%d h %d min", hoursPart, minutesPart) :
                String.format("%d min", minutesPart);
    }
}
