package org.labyrinth.footpath.converter;

import net.osmand.router.postman.RouteSegmentWithEquality;

import java.util.Set;
import java.util.stream.Collectors;

public class ConnectedRouteSegmentsWithinAreaProvider implements IConnectedRouteSegmentsProvider {

    private final IConnectedRouteSegmentsProvider delegate;
    private final RouteSegmentWithinAreaPredicate routeSegmentWithinAreaPredicate;

    public ConnectedRouteSegmentsWithinAreaProvider(
            final IConnectedRouteSegmentsProvider delegate,
            final RouteSegmentWithinAreaPredicate routeSegmentWithinAreaPredicate) {
        this.delegate = delegate;
        this.routeSegmentWithinAreaPredicate = routeSegmentWithinAreaPredicate;
    }

    @Override
    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
        return getRouteSegmentsWithinArea(delegate.getRouteSegmentsStartingAtEndOf(routeSegment));
    }

    @Override
    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
        return getRouteSegmentsWithinArea(delegate.getRouteSegmentsStartingAtStartOf(routeSegment));
    }

    private Set<RouteSegmentWithEquality> getRouteSegmentsWithinArea(final Set<RouteSegmentWithEquality> routeSegments) {
        return routeSegments
                .stream()
                .filter(routeSegment -> routeSegmentWithinAreaPredicate.isRouteSegmentWithinArea(routeSegment.delegate))
                .collect(Collectors.toSet());
    }
}
