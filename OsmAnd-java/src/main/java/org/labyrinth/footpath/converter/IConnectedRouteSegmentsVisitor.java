package org.labyrinth.footpath.converter;

import net.osmand.router.postman.RouteSegmentWithEquality;

import java.util.List;
import java.util.Set;

interface IConnectedRouteSegmentsVisitor<T> {

    T processConnectedRouteSegments(final RouteSegmentWithEquality start,
                                    final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart,
                                    final Set<RouteSegmentWithEquality> routeSegmentsStartingAtStartOfStart);

    T combine(final List<T> ts);
}
