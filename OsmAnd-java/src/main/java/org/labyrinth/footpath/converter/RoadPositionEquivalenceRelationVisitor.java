package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.postman.RouteSegmentWithEqualities;
import net.osmand.router.postman.RouteSegmentWithEquality;

import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Collections;
import java.util.List;
import java.util.Set;

class RoadPositionEquivalenceRelationVisitor implements IConnectedRouteSegmentsVisitor<Set<EquivalentRoadPositions>> {

    @Override
    public Set<EquivalentRoadPositions> processConnectedRouteSegments(
            final RouteSegmentWithEquality start,
            final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart,
            final Set<RouteSegmentWithEquality> routeSegmentsStartingAtStartOfStart) {
        return Collections.singleton(
                new EquivalentRoadPositions(
                        getEquivalentRoadPositions(
                                start,
                                routeSegmentsStartingAtEndOfStart)));
    }

    @Override
    public Set<EquivalentRoadPositions> combine(final List<Set<EquivalentRoadPositions>> equivalenceRelations) {
        return union(equivalenceRelations);
    }

    private static ImmutableSet<RoadPosition> getEquivalentRoadPositions(
            final RouteSegmentWithEquality start,
            final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart) {
        return ImmutableSet
                .<RoadPosition>builder()
                .add(RouteSegmentWithEqualities.getEndRoadPosition(start))
                .addAll(RouteSegmentWithEqualities.getStartRoadPositions(routeSegmentsStartingAtEndOfStart))
                .build();
    }
}
