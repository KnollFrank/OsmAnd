package org.labyrinth.footpath.graph;

import java.util.Objects;

public class RoadPosition {

    public final long road;
    public final int position;

    public RoadPosition(final long road, final int position) {
        this.road = road;
        this.position = position;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RoadPosition that = (RoadPosition) o;
        return road == that.road && position == that.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(road, position);
    }

    @Override
    public String toString() {
        return "RoadPosition{" +
                "road=" + road +
                ", position=" + position +
                '}';
    }
}
