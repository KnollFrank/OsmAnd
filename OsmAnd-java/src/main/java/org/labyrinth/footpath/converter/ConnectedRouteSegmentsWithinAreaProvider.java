package org.labyrinth.footpath.converter;

import net.osmand.router.postman.RouteSegmentWithEquality;

import java.util.Set;
import java.util.stream.Collectors;

public class ConnectedRouteSegmentsWithinAreaProvider implements IConnectedRouteSegmentsProvider {

    private final IConnectedRouteSegmentsProvider delegate;
    private final RouteSegmentPartlyWithinAreaPredicate routeSegmentPartlyWithinAreaPredicate;

    public ConnectedRouteSegmentsWithinAreaProvider(
            final IConnectedRouteSegmentsProvider delegate,
            final RouteSegmentPartlyWithinAreaPredicate routeSegmentPartlyWithinAreaPredicate) {
        this.delegate = delegate;
        this.routeSegmentPartlyWithinAreaPredicate = routeSegmentPartlyWithinAreaPredicate;
    }

    @Override
    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtEndOf(final RouteSegmentWithEquality routeSegment) {
        return getRouteSegmentsPartlyWithinArea(delegate.getRouteSegmentsStartingAtEndOf(routeSegment));
    }

    @Override
    public Set<RouteSegmentWithEquality> getRouteSegmentsStartingAtStartOf(final RouteSegmentWithEquality routeSegment) {
        return getRouteSegmentsPartlyWithinArea(delegate.getRouteSegmentsStartingAtStartOf(routeSegment));
    }

    private Set<RouteSegmentWithEquality> getRouteSegmentsPartlyWithinArea(final Set<RouteSegmentWithEquality> routeSegments) {
        return routeSegments
                .stream()
                .filter(routeSegment -> routeSegmentPartlyWithinAreaPredicate.isRouteSegmentPartlyWithinArea(routeSegment.delegate))
                .collect(Collectors.toSet());
    }
}
