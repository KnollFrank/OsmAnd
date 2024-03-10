package org.labyrinth.footpath.converter;

import static org.labyrinth.common.SetUtils.union;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.router.postman.RouteSegmentWithEquality;

import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class EdgesVisitor implements IConnectedRouteSegmentsVisitor<Set<Edge>> {

    private final Set<EquivalentRoadPositions> equivalenceRelation;

    public EdgesVisitor(final Set<EquivalentRoadPositions> equivalenceRelation) {
        this.equivalenceRelation = equivalenceRelation;
    }

    @Override
    public Set<Edge> processConnectedRouteSegments(
            final RouteSegmentWithEquality start,
            final Set<RouteSegmentWithEquality> routeSegmentsStartingAtEndOfStart,
            final Set<RouteSegmentWithEquality> routeSegmentsStartingAtStartOfStart) {
        return union(
                asEdges(routeSegmentsStartingAtStartOfStart),
                asEdges(routeSegmentsStartingAtEndOfStart));
    }

    @Override
    public Set<Edge> combine(final List<Set<Edge>> sets) {
        return union(sets);
    }

    private Set<Edge> asEdges(final Set<RouteSegmentWithEquality> routeSegments) {
        return routeSegments
                .stream()
                .map(routeSegment -> routeSegment.delegate)
                .map(this::asEdge)
                .collect(Collectors.toSet());
    }

    private Edge asEdge(final RouteSegment routeSegment) {
        return new Edge(
                getSourceNode(routeSegment),
                getTargetNode(routeSegment),
                Arrays.asList(routeSegment));
    }

    private Node getSourceNode(final RouteSegment routeSegment) {
        return getNode(routeSegment, routeSegment.getSegmentStart());
    }

    private Node getTargetNode(final RouteSegment routeSegment) {
        return getNode(routeSegment, routeSegment.getSegmentEnd());
    }

    private Node getNode(final RouteSegment routeSegment, final short position) {
        return new Node(
                GraphFactory.getEquivalentRoadPositions(new RoadPosition(routeSegment.getRoad().id, position), equivalenceRelation),
                RouteSegment2Geodetic.getGeodetic(routeSegment, position));
    }
}
