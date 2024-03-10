package org.labyrinth.footpath.converter;

import net.osmand.router.postman.RouteSegmentWithEquality;

import org.labyrinth.footpath.graph.EquivalentRoadPositions;

import java.util.Set;

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
}
