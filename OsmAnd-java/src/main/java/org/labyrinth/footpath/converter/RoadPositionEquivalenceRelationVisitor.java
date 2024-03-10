package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.postman.RouteSegmentWithEquality;

import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RoadPositionEquivalenceRelationVisitor implements IConnectedRouteSegmentsVisitor<Set<EquivalentRoadPositions>> {

    @Override
    public Set<EquivalentRoadPositions> processConnectedRouteSegments(
            final RouteSegmentWithEquality start,
            final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart,
            final Set<RouteSegmentWithEquality> routeSegmentsStartingAtStartOfStart) {
        final RoadPosition endOfStart = getEndRoadPosition(start.delegate);
        return ImmutableSet.of(
                new EquivalentRoadPositions(
                        ImmutableSet
                                .<RoadPosition>builder()
                                .add(endOfStart)
                                .addAll(
                                        routeSegmentsStartingAtEndOfStart
                                                .stream()
                                                .map(routeSegmentFromOtherRoadStartingAtEndOfStart -> routeSegmentFromOtherRoadStartingAtEndOfStart.delegate)
                                                .map(RoadPositionEquivalenceRelationVisitor::getStartRoadPosition)
                                                .collect(Collectors.toSet()))
                                .build()));
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
