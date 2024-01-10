package org.labyrinth.footpath.core;

import java.time.Duration;
import java.util.Objects;
import java.util.StringJoiner;

public class WalkingSpeed {

    public final int numberOfSteps;
    public final Duration duration;

    public WalkingSpeed(final int numberOfSteps, final Duration duration) {
        this.numberOfSteps = numberOfSteps;
        this.duration = duration;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final WalkingSpeed that = (WalkingSpeed) o;
        return numberOfSteps == that.numberOfSteps && duration.equals(that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfSteps, duration);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WalkingSpeed.class.getSimpleName() + "[", "]")
                .add("numberOfSteps=" + numberOfSteps)
                .add("duration=" + duration)
                .toString();
    }
}
