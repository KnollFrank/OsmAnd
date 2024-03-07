package org.labyrinth.footpath.converter;

import net.osmand.router.postman.RouteSegmentWithEquality;

import java.util.Set;

@FunctionalInterface
public interface IConnectedRouteSegmentsProvider {

    Set<RouteSegmentWithEquality> getConnectedRouteSegments(final RouteSegmentWithEquality routeSegment);
}
