package org.labyrinth.footpath.converter;

import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;
import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.RouteDataObject;
import net.osmand.util.MapUtils;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.common.ListUtils;
import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EquivalentRoadPositions;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphFactory {

    private final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider;

    public GraphFactory(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider) {
        this.connectedRouteSegmentsProvider = connectedRouteSegmentsProvider;
    }

    public Graph createGraph(final RouteSegmentWithEquality start) {
        final Set<EquivalentRoadPositions> equivalenceRelation = new RoadPositionEquivalenceRelationProvider(connectedRouteSegmentsProvider).getRoadPositionEquivalenceRelation(start);
        final Set<Edge> edges = getEdgesReachableFrom(start, equivalenceRelation);
        return new Graph(getNodes(edges), edges);
    }

    private Set<Edge> getEdgesReachableFrom(
            final RouteSegmentWithEquality start,
            final Set<EquivalentRoadPositions> equivalenceRelation) {
        Set<RouteSegmentWithEquality> routeSegments = Collections.singleton(start);
        Set<Edge> edges;
        boolean newRouteSegmentsFound;
        do {
            final Pair<Set<Edge>, Set<RouteSegmentWithEquality>> newEdgesAndNewRouteSegments = getEdgesAndRouteSegments(routeSegments, equivalenceRelation);
            final Set<RouteSegmentWithEquality> newRouteSegments = newEdgesAndNewRouteSegments.getSecond();
            edges = newEdgesAndNewRouteSegments.getFirst();
            newRouteSegmentsFound = !newRouteSegments.equals(routeSegments);
            routeSegments = newRouteSegments;
        } while (newRouteSegmentsFound);
        return edges;
    }

    private Pair<Set<Edge>, Set<RouteSegmentWithEquality>> getEdgesAndRouteSegments(
            final Set<RouteSegmentWithEquality> routeSegments,
            final Set<EquivalentRoadPositions> equivalenceRelation) {
        final List<Pair<Set<Edge>, Set<RouteSegmentWithEquality>>> edgesAndRouteSegmentsList =
                routeSegments
                        .stream()
                        .map(routeSegment -> getEdgesAndRouteSegments(routeSegment, equivalenceRelation))
                        .collect(Collectors.toList());
        return Pair.of(
                getEdges(edgesAndRouteSegmentsList),
                getRouteSegments(edgesAndRouteSegmentsList));
    }

    private Pair<Set<Edge>, Set<RouteSegmentWithEquality>> getEdgesAndRouteSegments(
            final RouteSegmentWithEquality start,
            final Set<EquivalentRoadPositions> equivalenceRelation) {
        final Set<RouteSegmentWithEquality> routeSegments = connectedRouteSegmentsProvider.getConnectedRouteSegments(start);
        return Pair.of(asEdges(routeSegments, equivalenceRelation), routeSegments);
    }

    private static Set<Edge> asEdges(
            final Set<RouteSegmentWithEquality> routeSegments,
            final Set<EquivalentRoadPositions> equivalenceRelation) {
        return routeSegments
                .stream()
                .map(routeSegmentWrapper -> routeSegmentWrapper.delegate)
                .map(routeSegment -> asEdge(routeSegment, equivalenceRelation))
                .collect(Collectors.toSet());
    }

    private static Edge asEdge(final RouteSegment routeSegment, final Set<EquivalentRoadPositions> equivalenceRelation) {
        return new Edge(
                getSourceNode(routeSegment, equivalenceRelation),
                getTargetNode(routeSegment, equivalenceRelation),
                Arrays.asList(routeSegment));
    }

    private static Set<Node> getNodes(final Set<Edge> edges) {
        return edges
                .stream()
                .flatMap(edge -> Stream.of(edge.source, edge.target))
                .collect(Collectors.toSet());
    }

    private static Node getSourceNode(final RouteSegment routeSegment, final Set<EquivalentRoadPositions> equivalenceRelation) {
        return getNode(routeSegment, routeSegment.getSegmentStart(), equivalenceRelation);
    }

    private static Node getTargetNode(final RouteSegment routeSegment, final Set<EquivalentRoadPositions> equivalenceRelation) {
        return getNode(routeSegment, routeSegment.getSegmentEnd(), equivalenceRelation);
    }

    private static Node getNode(
            final RouteSegment routeSegment,
            final short position,
            final Set<EquivalentRoadPositions> equivalenceRelation) {
        return new Node(
                getEquivalentRoadPositions(new RoadPosition(routeSegment.getRoad().id, position), equivalenceRelation),
                getGeodetic(routeSegment, position));
    }

    private static Geodetic getGeodetic(final RouteSegment routeSegment, final short i) {
        return getGeodetic(routeSegment.getRoad(), i);
    }

    private static Geodetic getGeodetic(final RouteDataObject road, final short i) {
        return new Geodetic(getLatitude(road, i), getLongitude(road, i));
    }

    private static Angle getLatitude(final RouteDataObject road, final short i) {
        return new Angle(MapUtils.get31LatitudeY(road.getPoint31YTile(i)), Angle.Unit.DEGREES);
    }

    private static Angle getLongitude(final RouteDataObject road, final short i) {
        return new Angle(MapUtils.get31LongitudeX(road.getPoint31XTile(i)), Angle.Unit.DEGREES);
    }

    private static Set<Edge> getEdges(final List<Pair<Set<Edge>, Set<RouteSegmentWithEquality>>> edgesAndRouteSegmentsList) {
        return union(ListUtils.getFirsts(edgesAndRouteSegmentsList));
    }

    private static Set<RouteSegmentWithEquality> getRouteSegments(final List<Pair<Set<Edge>, Set<RouteSegmentWithEquality>>> edgesAndRouteSegmentsList) {
        return union(ListUtils.getSeconds(edgesAndRouteSegmentsList));
    }

    static EquivalentRoadPositions getEquivalentRoadPositions(final RoadPosition roadPosition, final Set<EquivalentRoadPositions> equivalenceRelation) {
        return equivalenceRelation
                .stream()
                .filter(equivalentRoadPositions -> equivalentRoadPositions.roadPositions.contains(roadPosition))
                .findFirst()
                .orElseGet(() -> new EquivalentRoadPositions(ImmutableSet.of(roadPosition)));
    }
}
