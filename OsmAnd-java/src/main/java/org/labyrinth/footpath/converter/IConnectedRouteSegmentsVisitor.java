package org.labyrinth.footpath.converter;

import net.osmand.router.postman.RouteSegmentWithEquality;

import java.util.List;
import java.util.Set;

interface IConnectedRouteSegmentsVisitor<T> {

    T processConnectedRouteSegments(final RouteSegmentWithEquality source,
                                    final Set<RouteSegmentWithEquality> destinations);

    T combine(final List<T> ts);
}
