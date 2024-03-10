package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.popAny;
import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets;

import net.osmand.binary.RouteDataObject;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.postman.RouteSegmentWithEquality;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.common.Utils;

import java.util.HashSet;
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
        final Set<RouteSegmentWithEquality> routeSegments2Process = Sets.newHashSet(start);
        final Set<RouteSegmentWithEquality> routeSegmentsAlreadyProcessed = new HashSet<>();
        final Builder<T> tsBuilder = ImmutableList.builder();
        RouteSegmentWithEquality routeSegment2Process;
        while ((routeSegment2Process = popAny(routeSegments2Process)) != null) {
            if (!routeSegmentsAlreadyProcessed.contains(routeSegment2Process)) {
                final Pair<T, Set<RouteSegmentWithEquality>> t_routeSegments2Process = process(routeSegment2Process);
                routeSegmentsAlreadyProcessed.add(routeSegment2Process);
                tsBuilder.add(t_routeSegments2Process.getFirst());
                routeSegments2Process.addAll(t_routeSegments2Process.getSecond());
                routeSegments2Process.removeAll(routeSegmentsAlreadyProcessed);
            }
        }
        return connectedRouteSegmentsVisitor.combine(tsBuilder.build());
    }

    private Pair<T, Set<RouteSegmentWithEquality>> process(final RouteSegmentWithEquality start) {
        final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart = connectedRouteSegmentsProvider.getRouteSegmentsStartingAtEndOf(start);
        final Set<RouteSegmentWithEquality> routeSegmentsStartingAtStartOfStart = connectedRouteSegmentsProvider.getRouteSegmentsStartingAtStartOf(start);
        return Pair.of(
                connectedRouteSegmentsVisitor.processConnectedRouteSegments(
                        start,
                        routeSegmentsStartingAtEndOfStart,
                        routeSegmentsStartingAtStartOfStart),
                getRouteSegments2Process(union(routeSegmentsStartingAtEndOfStart, routeSegmentsStartingAtStartOfStart)));
    }

    private static Set<RouteSegmentWithEquality> getRouteSegments2Process(final Set<RouteSegmentWithEquality> routeSegments) {
        final Set<RouteSegmentWithEquality> explodedRouteSegments = explode(routeSegments);
        return union(
                routeSegments,
                explodedRouteSegments,
                reverse(explodedRouteSegments));
    }

    private static Set<RouteSegmentWithEquality> explode(final Set<RouteSegmentWithEquality> routeSegments) {
        return routeSegments
                .stream()
                .flatMap(routeSegment -> explode(routeSegment.delegate.getRoad()).stream())
                .collect(Collectors.toSet());
    }

    private static Set<RouteSegmentWithEquality> explode(final RouteDataObject road) {
        return Utils
                .getConsecutivePairs(0, road.getPointsLength() - 1)
                .map(segmentStart_segmentEnd ->
                        new RouteSegment(
                                road,
                                segmentStart_segmentEnd.getFirst(),
                                segmentStart_segmentEnd.getSecond()))
                .map(RouteSegmentWithEquality::new)
                .collect(Collectors.toSet());
    }

    private static Set<RouteSegmentWithEquality> reverse(final Set<RouteSegmentWithEquality> routeSegments) {
        return routeSegments
                .stream()
                .map(RouteSegmentWithEquality::reverse)
                .collect(Collectors.toSet());
    }
}
