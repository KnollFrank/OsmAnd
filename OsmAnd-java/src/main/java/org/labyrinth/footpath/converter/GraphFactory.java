package org.labyrinth.footpath.converter;

import static net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;

import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.Graph;

import java.util.Set;

public class GraphFactory {

    private final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider;

    public GraphFactory(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider) {
        this.connectedRouteSegmentsProvider = connectedRouteSegmentsProvider;
    }

    public Graph createGraph(final RouteSegmentWithEquality start) {
        final ConnectedRouteSegmentsProcessor<Set<Edge>> connectedRouteSegmentsProcessor =
                new ConnectedRouteSegmentsProcessor<>(
                        connectedRouteSegmentsProvider,
                        new EdgesVisitor(getRoadPositionEquivalenceRelation(start)));
        final Set<Edge> edges = connectedRouteSegmentsProcessor.processConnectedRouteSegments(start);
        return org.labyrinth.footpath.graph.GraphFactory.createGraph(edges);
    }

    private Set<EquivalentRoadPositions> getRoadPositionEquivalenceRelation(final RouteSegmentWithEquality start) {
        return new RoadPositionEquivalenceRelationProvider(connectedRouteSegmentsProvider).getRoadPositionEquivalenceRelation(start);
    }
}
