package org.labyrinth.footpath.converter;

import static net.osmand.router.PostmanTourPlanner.isSameRoad;
import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;

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

    private static class RoadPositionEquivalenceRelationVisitor implements IConnectedRouteSegmentsVisitor<Set<EquivalentRoadPositions>> {

        @Override
        public Set<EquivalentRoadPositions> processConnectedRouteSegments(
                final RouteSegmentWithEquality source,
                final Set<RouteSegmentWithEquality> destinations) {
            final RoadPosition endOfSource = getEndRoadPosition(source.delegate);
            return destinations
                    .stream()
                    .filter(routeSegment -> !isSameRoad(routeSegment, source))
                    .map(routeSegmentFromOtherRoad ->
                            new EquivalentRoadPositions(
                                    ImmutableSet.of(
                                            endOfSource,
                                            getStartRoadPosition(routeSegmentFromOtherRoad.delegate))))
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
