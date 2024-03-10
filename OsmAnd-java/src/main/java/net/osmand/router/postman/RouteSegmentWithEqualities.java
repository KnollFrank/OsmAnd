package net.osmand.router.postman;

import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Set;
import java.util.stream.Collectors;

public class RouteSegmentWithEqualities {

    public static Set<RoadPosition> getStartRoadPositions(final Set<RouteSegmentWithEquality> routeSegments) {
        return routeSegments
                .stream()
                .map(RouteSegmentWithEqualities::getStartRoadPosition)
                .collect(Collectors.toSet());
    }

    public static RoadPosition getStartRoadPosition(final RouteSegmentWithEquality routeSegment) {
        return new RoadPosition(
                routeSegment.delegate.getRoad().id,
                routeSegment.delegate.getSegmentStart());
    }

    public static RoadPosition getEndRoadPosition(final RouteSegmentWithEquality routeSegment) {
        return new RoadPosition(
                routeSegment.delegate.getRoad().id,
                routeSegment.delegate.getSegmentEnd());
    }
}
