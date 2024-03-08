package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.RouteDataObject;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.postman.RouteSegmentWithEquality;

import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RoadPositionEquivalenceRelationProvider {

    private final ConnectedRouteSegmentsProcessor<Set<EquivalentRoadPositions>> connectedRouteSegmentsProcessor;

    public RoadPositionEquivalenceRelationProvider(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider) {
        this.connectedRouteSegmentsProcessor =
                new ConnectedRouteSegmentsProcessor<>(
                        connectedRouteSegmentsProvider,
                        new RoadPositionEquivalenceRelationVisitor());
    }

    public final Set<EquivalentRoadPositions> getRoadPositionEquivalenceRelation(final RouteSegmentWithEquality start) {
        return connectedRouteSegmentsProcessor.processConnectedRouteSegments(start);
    }

    private static boolean isSameRoad(final RouteSegmentWithEquality routeSegment1, final RouteSegmentWithEquality routeSegment2) {
        return isSameRoad(routeSegment1.delegate.getRoad(), routeSegment2.delegate.getRoad());
    }

    private static boolean isSameRoad(final RouteDataObject road1, final RouteDataObject road2) {
        return road1.id == road2.id;
    }

    private static class RoadPositionEquivalenceRelationVisitor implements IConnectedRouteSegmentsVisitor<Set<EquivalentRoadPositions>> {

        @Override
        public Set<EquivalentRoadPositions> processConnectedRouteSegments(
                final RouteSegmentWithEquality start,
                final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart,
                final Set<RouteSegmentWithEquality> routeSegmentsStartingAtStartOfStart) {
            final RoadPosition endOfStart = getEndRoadPosition(start.delegate);
            return routeSegmentsStartingAtEndOfStart
                    .stream()
                    .filter(routeSegmentStartingAtEndOfStart -> !isSameRoad(routeSegmentStartingAtEndOfStart, start))
                    .map(routeSegmentFromOtherRoadStartingAtEndOfStart ->
                            new EquivalentRoadPositions(
                                    ImmutableSet.of(
                                            endOfStart,
                                            getStartRoadPosition(routeSegmentFromOtherRoadStartingAtEndOfStart.delegate))))
                    .collect(Collectors.toSet());
        }

        @Override
        public Set<EquivalentRoadPositions> combine(final List<Set<EquivalentRoadPositions>> equivalenceRelations) {
            return union(equivalenceRelations);
        }

        private static RoadPosition getStartRoadPosition(final RouteSegment routeSegment) {
            return new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentStart());
        }

        private static RoadPosition getEndRoadPosition(final RouteSegment routeSegment) {
            return new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentEnd());
        }
    }
}
