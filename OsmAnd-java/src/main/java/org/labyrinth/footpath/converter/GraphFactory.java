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
        return org.labyrinth.footpath.graph.GraphFactory.createGraph(getEdges(start));
    }

    private Set<Edge> getEdges(final RouteSegmentWithEquality start) {
        return this
                .createConnectedRouteSegmentsProcessor(start)
                .processConnectedRouteSegments(start);
    }

    private ConnectedRouteSegmentsProcessor<Set<Edge>> createConnectedRouteSegmentsProcessor(final RouteSegmentWithEquality start) {
        return new ConnectedRouteSegmentsProcessor<>(
                connectedRouteSegmentsProvider,
                new EdgesVisitor(getRoadPositionEquivalenceRelation(start)));
    }

    private Set<EquivalentRoadPositions> getRoadPositionEquivalenceRelation(final RouteSegmentWithEquality start) {
        return new RoadPositionEquivalenceRelationProvider(connectedRouteSegmentsProvider).getRoadPositionEquivalenceRelation(start);
    }
}
