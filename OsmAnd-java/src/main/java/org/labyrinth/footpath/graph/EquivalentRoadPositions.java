package org.labyrinth.footpath.graph;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

public class EquivalentRoadPositions implements Comparable<EquivalentRoadPositions> {

    public final ImmutableSet<RoadPosition> roadPositions;

    public EquivalentRoadPositions(final ImmutableSet<RoadPosition> roadPositions) {
        if (roadPositions.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.roadPositions = roadPositions;
    }

    @Override
    public int compareTo(final EquivalentRoadPositions equivalentRoadPositions) {
        return getRepresentative(roadPositions).compareTo(getRepresentative(equivalentRoadPositions.roadPositions));
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

    private RoadPosition getRepresentative(final Set<RoadPosition> roadPositions) {
        return roadPositions.stream().sorted().findFirst().get();
    }
}
