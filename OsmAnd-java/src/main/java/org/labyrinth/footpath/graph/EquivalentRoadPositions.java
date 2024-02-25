package org.labyrinth.footpath.graph;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;

public class EquivalentRoadPositions {

    public final ImmutableSet<RoadPosition> roadPositions;

    public EquivalentRoadPositions(final ImmutableSet<RoadPosition> roadPositions) {
        this.roadPositions = roadPositions;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EquivalentRoadPositions that = (EquivalentRoadPositions) o;
        return Objects.equals(roadPositions, that.roadPositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roadPositions);
    }

    @Override
    public String toString() {
        return "EquivalentRoadPositions{" +
                "roadPositions=" + roadPositions +
                '}';
    }
}
