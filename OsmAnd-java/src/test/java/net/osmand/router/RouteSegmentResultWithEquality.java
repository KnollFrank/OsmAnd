package net.osmand.router;

import java.util.Objects;

public class RouteSegmentResultWithEquality {

    private final long road;
    private final String roadName;
    private final int startPointIndex;
    private final int endPointIndex;

    public RouteSegmentResultWithEquality(final long road,
                                          final String roadName,
                                          final int startPointIndex,
                                          final int endPointIndex) {
        this.road = road;
        this.roadName = roadName;
        this.startPointIndex = startPointIndex;
        this.endPointIndex = endPointIndex;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RouteSegmentResultWithEquality that = (RouteSegmentResultWithEquality) o;
        return road == that.road && startPointIndex == that.startPointIndex && endPointIndex == that.endPointIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(road, startPointIndex, endPointIndex);
    }

    @Override
    public String toString() {
        return "RouteSegmentResultWithEquality{" +
                "road=" + road +
                ", roadName='" + roadName + '\'' +
                ", startPointIndex=" + startPointIndex +
                ", endPointIndex=" + endPointIndex +
                '}';
    }
}
