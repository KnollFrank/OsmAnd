package org.labyrinth.footpath.converter;

import static org.labyrinth.footpath.converter.RouteSegment2Geodetic.getEnd;
import static org.labyrinth.footpath.converter.RouteSegment2Geodetic.getStart;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;

public class RouteSegmentWithinCirclePredicate implements RouteSegmentWithinAreaPredicate {

    private final Circle circle;

    public RouteSegmentWithinCirclePredicate(final Circle circle) {
        this.circle = circle;
    }

    @Override
    public boolean isRouteSegmentWithinArea(final RouteSegment routeSegment) {
        return circle.contains(getStart(routeSegment)) && circle.contains(getEnd(routeSegment));
    }
}
