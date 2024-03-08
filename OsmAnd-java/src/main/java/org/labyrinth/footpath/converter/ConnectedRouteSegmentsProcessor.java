package org.labyrinth.footpath.converter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets;

import net.osmand.router.postman.RouteSegmentWithEquality;

import org.labyrinth.common.Utils;

import java.util.HashSet;
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
        final Set<RouteSegmentWithEquality> routeSegments2Process = Sets.newHashSet(start);
        final Set<RouteSegmentWithEquality> routeSegmentsAlreadyProcessed = new HashSet<>();
        final Builder<T> tsBuilder = ImmutableList.builder();
        while (!routeSegments2Process.isEmpty()) {
            final RouteSegmentWithEquality routeSegment2Process = Utils.getAny(routeSegments2Process);
            if (!routeSegmentsAlreadyProcessed.contains(routeSegment2Process)) {
                final T t = _processConnectedRouteSegments(routeSegment2Process, routeSegments2Process);
                tsBuilder.add(t);
                routeSegmentsAlreadyProcessed.add(routeSegment2Process);
            }
            routeSegments2Process.removeAll(routeSegmentsAlreadyProcessed);
        }
        return connectedRouteSegmentsVisitor.combine(tsBuilder.build());
    }

    private T _processConnectedRouteSegments(final RouteSegmentWithEquality start, final Set<RouteSegmentWithEquality> routeSegments2Process) {
        final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart = connectedRouteSegmentsProvider.getRouteSegmentsStartingAtEndOf(start);
        final Set<RouteSegmentWithEquality> routeSegmentsStartingAtStartOfStart = connectedRouteSegmentsProvider.getRouteSegmentsStartingAtStartOf(start);
        final T t =
                connectedRouteSegmentsVisitor.processConnectedRouteSegments(
                        start,
                        routeSegmentsStartingAtEndOfStart,
                        routeSegmentsStartingAtStartOfStart);
        routeSegments2Process.addAll(routeSegmentsStartingAtEndOfStart);
        routeSegments2Process.addAll(routeSegmentsStartingAtStartOfStart);
        return t;
    }
}
