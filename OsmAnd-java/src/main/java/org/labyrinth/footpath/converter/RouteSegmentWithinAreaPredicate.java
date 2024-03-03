package org.labyrinth.footpath.converter;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;

@FunctionalInterface
public interface RouteSegmentWithinAreaPredicate {

    boolean isRouteSegmentWithinArea(RouteSegment routeSegment);
}
