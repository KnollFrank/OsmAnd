package org.labyrinth.footpath.converter;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.postman.RouteSegmentWithEquality;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ConnectedRouteSegmentsProcessor<T> {

    private final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider;
    private final IConnectedRouteSegmentsVisitor<T> connectedRouteSegmentsVisitor;

    public ConnectedRouteSegmentsProcessor(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider,
                                           final IConnectedRouteSegmentsVisitor<T> connectedRouteSegmentsVisitor) {
        this.connectedRouteSegmentsProvider = connectedRouteSegmentsProvider;
        this.connectedRouteSegmentsVisitor = connectedRouteSegmentsVisitor;
    }

    public final T processConnectedRouteSegments(final RouteSegmentWithEquality start) {
        final Set<RouteSegmentWithEquality> routeSegments2Process = new HashSet<>();
        routeSegments2Process.add(start);
        final Set<RouteSegmentWithEquality> routeSegmentsAlreadyProcessed = new HashSet<>();
        final List<T> ts = new ArrayList<>();
        while (!routeSegments2Process.isEmpty()) {
            final RouteSegmentWithEquality routeSegment2Process = routeSegments2Process.stream().findAny().get();
            if (!routeSegmentsAlreadyProcessed.contains(routeSegment2Process)) {
                ts.add(getT(routeSegment2Process, routeSegments2Process));
                routeSegmentsAlreadyProcessed.add(routeSegment2Process);
            }
            routeSegments2Process.removeAll(routeSegmentsAlreadyProcessed);
        }
        return connectedRouteSegmentsVisitor.combine(ts);
    }

    private T getT(final RouteSegmentWithEquality start, final Set<RouteSegmentWithEquality> routeSegments2Process) {
        final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart = connectedRouteSegmentsProvider.getRouteSegmentsStartingAtEndOf(start);
        final Set<RouteSegmentWithEquality> routeSegmentsStartingAtStartOfStart = connectedRouteSegmentsProvider.getRouteSegmentsStartingAtStartOf(start);
        final T t =
                connectedRouteSegmentsVisitor.processConnectedRouteSegments(
                        start,
                        routeSegmentsStartingAtEndOfStart,
                        routeSegmentsStartingAtStartOfStart);
        final Set<RouteSegmentWithEquality> routeSegments =
                ImmutableSet
                        .<RouteSegmentWithEquality>builder()
                        .addAll(routeSegmentsStartingAtEndOfStart)
                        .addAll(routeSegmentsStartingAtStartOfStart)
                        .build();
        routeSegments2Process.addAll(routeSegments);
        return t;
    }
}
