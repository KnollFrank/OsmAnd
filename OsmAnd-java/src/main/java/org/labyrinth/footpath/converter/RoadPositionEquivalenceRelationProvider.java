package org.labyrinth.footpath.converter;

import static net.osmand.router.PostmanTourPlanner.isSameRoad;
import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.common.ListUtils;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RoadPositionEquivalenceRelationProvider {

    private final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider;

    public RoadPositionEquivalenceRelationProvider(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider) {
        this.connectedRouteSegmentsProvider = connectedRouteSegmentsProvider;
    }

    // FK-TODO: use com.google.common.base.Equivalence?
    public Set<EquivalentRoadPositions> getRoadPositionEquivalenceRelation(final RouteSegmentWithEquality start) {
        Set<RouteSegmentWithEquality> routeSegments = Collections.singleton(start);
        Set<EquivalentRoadPositions> equivalenceRelation;
        boolean newRouteSegmentsFound;
        do {
            final Pair<Set<EquivalentRoadPositions>, Set<RouteSegmentWithEquality>> newEquivalenceRelationAndRouteSegments = getEquivalenceRelationAndRouteSegments(routeSegments);
            final Set<RouteSegmentWithEquality> newRouteSegments = newEquivalenceRelationAndRouteSegments.getSecond();
            equivalenceRelation = newEquivalenceRelationAndRouteSegments.getFirst();
            newRouteSegmentsFound = !newRouteSegments.equals(routeSegments);
            routeSegments = newRouteSegments;
        } while (newRouteSegmentsFound);
        return equivalenceRelation;
    }

    private Pair<Set<EquivalentRoadPositions>, Set<RouteSegmentWithEquality>> getEquivalenceRelationAndRouteSegments(final Set<RouteSegmentWithEquality> routeSegments) {
        final List<Pair<Set<EquivalentRoadPositions>, Set<RouteSegmentWithEquality>>> equivalenceRelationAndRouteSegmentsList =
                routeSegments
                        .stream()
                        .map(this::getEquivalenceRelationAndRouteSegments)
                        .collect(Collectors.toList());
        return Pair.of(
                getEquivalenceRelation(equivalenceRelationAndRouteSegmentsList),
                getRouteSegments(equivalenceRelationAndRouteSegmentsList));
    }

    private Pair<Set<EquivalentRoadPositions>, Set<RouteSegmentWithEquality>> getEquivalenceRelationAndRouteSegments(final RouteSegmentWithEquality start) {
        final Set<RouteSegmentWithEquality> routeSegments = connectedRouteSegmentsProvider.getConnectedRouteSegments(start);
        return Pair.of(getEquivalenceForStartToOtherRoad(start, routeSegments), routeSegments);
    }

    private static Set<EquivalentRoadPositions> getEquivalenceForStartToOtherRoad(final RouteSegmentWithEquality start,
                                                                                  final Set<RouteSegmentWithEquality> routeSegments) {
        final RoadPosition endOfStart = getEndRoadPosition(start.delegate);
        return routeSegments
                .stream()
                .filter(routeSegment -> !isSameRoad(routeSegment, start))
                .map(routeSegmentFromOtherRoad ->
                        new EquivalentRoadPositions(
                                ImmutableSet.of(
                                        endOfStart,
                                        getStartRoadPosition(routeSegmentFromOtherRoad.delegate))))
                .collect(Collectors.toSet());
    }

    private static RoadPosition getStartRoadPosition(final RouteSegment routeSegment) {
        return new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentStart());
    }

    private static RoadPosition getEndRoadPosition(final RouteSegment routeSegment) {
        return new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentEnd());
    }

    private static Set<EquivalentRoadPositions> getEquivalenceRelation(final List<Pair<Set<EquivalentRoadPositions>, Set<RouteSegmentWithEquality>>> equivalenceRelationAndRouteSegmentsList) {
        return union(ListUtils.getFirsts(equivalenceRelationAndRouteSegmentsList));
    }

    private static Set<RouteSegmentWithEquality> getRouteSegments(final List<Pair<Set<EquivalentRoadPositions>, Set<RouteSegmentWithEquality>>> equivalenceRelationAndRouteSegmentsList) {
        return union(ListUtils.getSeconds(equivalenceRelationAndRouteSegmentsList));
    }
}
