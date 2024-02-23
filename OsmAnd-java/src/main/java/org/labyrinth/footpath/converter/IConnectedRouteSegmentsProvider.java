package org.labyrinth.footpath.converter;

import net.osmand.router.PostmanTourPlanner.RouteSegmentWrapper;

import java.util.Set;

@FunctionalInterface
public interface IConnectedRouteSegmentsProvider {

    Set<RouteSegmentWrapper> getConnectedRouteSegments(final RouteSegmentWrapper routeSegment);
}
