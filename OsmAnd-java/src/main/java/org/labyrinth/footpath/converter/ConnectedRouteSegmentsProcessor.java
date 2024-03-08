package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.postman.RouteSegmentWithEquality;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.common.ListUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ConnectedRouteSegmentsProcessor<T> {

    private final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider;
    private final IConnectedRouteSegmentsVisitor<T> connectedRouteSegmentsVisitor;

    public ConnectedRouteSegmentsProcessor(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider,
                                           final IConnectedRouteSegmentsVisitor<T> connectedRouteSegmentsVisitor) {
        this.connectedRouteSegmentsProvider = connectedRouteSegmentsProvider;
        this.connectedRouteSegmentsVisitor = connectedRouteSegmentsVisitor;
    }

    public final T processConnectedRouteSegments(final RouteSegmentWithEquality start) {
        Set<RouteSegmentWithEquality> routeSegments = Collections.singleton(start);
        T t;
        boolean newRouteSegmentsFound;
        do {
            final Pair<T, Set<RouteSegmentWithEquality>> newTAndRouteSegments = getTAndRouteSegments(routeSegments);
            final Set<RouteSegmentWithEquality> newRouteSegments = newTAndRouteSegments.getSecond();
            t = newTAndRouteSegments.getFirst();
            newRouteSegmentsFound = !newRouteSegments.equals(routeSegments);
            routeSegments = newRouteSegments;
        } while (newRouteSegmentsFound);
        return t;
    }

    private Pair<T, Set<RouteSegmentWithEquality>> getTAndRouteSegments(final Set<RouteSegmentWithEquality> routeSegments) {
        final List<Pair<T, Set<RouteSegmentWithEquality>>> tAndRouteSegmentsList =
                routeSegments
                        .stream()
                        .map(this::getTAndRouteSegments)
                        .collect(Collectors.toList());
        return Pair.of(
                getT(tAndRouteSegmentsList),
                getRouteSegments(tAndRouteSegmentsList));
    }

    private Pair<T, Set<RouteSegmentWithEquality>> getTAndRouteSegments(final RouteSegmentWithEquality start) {
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
        return Pair.of(t, routeSegments);
    }

    private T getT(final List<Pair<T, Set<RouteSegmentWithEquality>>> tAndRouteSegmentsList) {
        return connectedRouteSegmentsVisitor.combine(ListUtils.getFirsts(tAndRouteSegmentsList));
    }

    private Set<RouteSegmentWithEquality> getRouteSegments(final List<Pair<T, Set<RouteSegmentWithEquality>>> tAndRouteSegmentsList) {
        return union(ListUtils.getSeconds(tAndRouteSegmentsList));
    }
}
