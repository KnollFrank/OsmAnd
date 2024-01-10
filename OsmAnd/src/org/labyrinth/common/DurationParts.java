package org.labyrinth.common;

import java.time.Duration;

class DurationParts {

    public static long getHoursPart(final Duration duration) {
        return duration.getSeconds() / 3600;
    }

    public static long getMinutesPart(final Duration duration) {
        return (duration.getSeconds() % 3600) / 60;
    }

    public static long getSecondsPart(final Duration duration) {
        return duration.getSeconds() % 60;
    }
}
