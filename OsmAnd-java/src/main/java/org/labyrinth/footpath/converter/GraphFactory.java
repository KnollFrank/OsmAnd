package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.router.postman.RouteSegmentWithEquality;

import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Edges;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Set;

public class GraphFactory {

    private final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider;

    public GraphFactory(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider) {
        this.connectedRouteSegmentsProvider = connectedRouteSegmentsProvider;
    }

    public Graph createGraph(final RouteSegmentWithEquality start) {
        return org.labyrinth.footpath.graph.GraphFactory.createGraph(addReversedEdges(getEdges(start)));
    }

    private Set<Edge> addReversedEdges(final Set<Edge> edges) {
        return union(edges, Edges.reverse(edges));
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

    static EquivalentRoadPositions getEquivalentRoadPositions(final RoadPosition roadPosition, final Set<EquivalentRoadPositions> equivalenceRelation) {
        return equivalenceRelation
                .stream()
                .filter(equivalentRoadPositions -> equivalentRoadPositions.roadPositions.contains(roadPosition))
                .findFirst()
                .orElseGet(() -> new EquivalentRoadPositions(ImmutableSet.of(roadPosition)));
    }
}
