package org.labyrinth.footpath.converter;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;

@FunctionalInterface
public interface RouteSegmentPartlyWithinAreaPredicate {

    boolean isRouteSegmentPartlyWithinArea(RouteSegment routeSegment);
}
