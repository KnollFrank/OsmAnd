package org.labyrinth.footpath.converter;

import net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;

import java.util.Set;

@FunctionalInterface
public interface IConnectedRouteSegmentsProvider {

    Set<RouteSegmentWithEquality> getConnectedRouteSegments(final RouteSegmentWithEquality routeSegment);
}
