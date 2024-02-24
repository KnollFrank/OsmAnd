package org.labyrinth.footpath.converter;

import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;
import static net.osmand.router.PostmanTourPlanner.isSameRoad;
import static org.labyrinth.common.SetUtils.union;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.RouteDataObject;
import net.osmand.util.MapUtils;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

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
        final Set<Edge> edges = getEdgesReachableFrom(start);
        return new Graph(getNodes(edges), edges);
    }

    private Set<Edge> getEdgesReachableFrom(final RouteSegmentWithEquality start) {
        Set<RouteSegmentWithEquality> routeSegments = Collections.singleton(start);
        Set<Edge> edges;
        boolean newRouteSegmentsFound;
        do {
            final Pair<Set<Edge>, Set<RouteSegmentWithEquality>> newEdgesAndNewRouteSegments = getEdgesAndRouteSegments(routeSegments);
            final Set<RouteSegmentWithEquality> newRouteSegments = newEdgesAndNewRouteSegments.getSecond();
            edges = newEdgesAndNewRouteSegments.getFirst();
            newRouteSegmentsFound = !newRouteSegments.equals(routeSegments);
            routeSegments = newRouteSegments;
        } while (newRouteSegmentsFound);
        return edges;
    }

    private Pair<Set<Edge>, Set<RouteSegmentWithEquality>> getEdgesAndRouteSegments(final Set<RouteSegmentWithEquality> routeSegments) {
        final List<Pair<Set<Edge>, Set<RouteSegmentWithEquality>>> edgesAndRouteSegmentsList =
                routeSegments
                        .stream()
                        .map(this::getEdgesAndRouteSegments)
                        .collect(Collectors.toList());
        return Pair.of(
                getEdges(edgesAndRouteSegmentsList),
                getRouteSegments(edgesAndRouteSegmentsList));
    }

    private Pair<Set<Edge>, Set<RouteSegmentWithEquality>> getEdgesAndRouteSegments(final RouteSegmentWithEquality start) {
        final Set<RouteSegmentWithEquality> routeSegments = connectedRouteSegmentsProvider.getConnectedRouteSegments(start);
        return Pair.of(getEdges(start, routeSegments), routeSegments);
    }

    private static Set<Edge> getEdges(final RouteSegmentWithEquality start, final Set<RouteSegmentWithEquality> routeSegments) {
        return ImmutableSet
                .<Edge>builder()
                .addAll(asEdges(routeSegments))
                .addAll(getEdgesFromStartToOtherRoad(start, routeSegments))
                .build();
    }

    private static Set<Edge> getEdgesFromStartToOtherRoad(final RouteSegmentWithEquality start,
                                                          final Set<RouteSegmentWithEquality> routeSegments) {
        final Node targetOfStart = getTargetNode(start.delegate);
        return routeSegments
                .stream()
                .filter(routeSegment -> !isSameRoad(routeSegment, start))
                .map(routeSegmentFromOtherRoad -> getSourceNode(routeSegmentFromOtherRoad.delegate))
                .map(sourceOfOtherRoad -> new Edge(targetOfStart, sourceOfOtherRoad))
                .collect(Collectors.toSet());
    }

    private static Set<Node> getNodes(final Set<Edge> edges) {
        return edges
                .stream()
                .flatMap(edge -> Stream.of(edge.source, edge.target))
                .collect(Collectors.toSet());
    }

    private static Set<Edge> asEdges(final Set<RouteSegmentWithEquality> routeSegments) {
        return routeSegments
                .stream()
                .map(routeSegmentWrapper -> routeSegmentWrapper.delegate)
                .map(GraphFactory::asEdge)
                .collect(Collectors.toSet());
    }

    private static Edge asEdge(final RouteSegment routeSegment) {
        return new Edge(getSourceNode(routeSegment), getTargetNode(routeSegment));
    }

    private static Node getSourceNode(final RouteSegment routeSegment) {
        return new Node(
                new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentStart()),
                getGeodetic(routeSegment, routeSegment.getSegmentStart()));
    }

    private static Node getTargetNode(final RouteSegment routeSegment) {
        return new Node(
                new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentEnd()),
                getGeodetic(routeSegment, routeSegment.getSegmentEnd()));
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
        return union(getFirstList(edgesAndRouteSegmentsList));
    }

    private static Set<RouteSegmentWithEquality> getRouteSegments(final List<Pair<Set<Edge>, Set<RouteSegmentWithEquality>>> edgesAndRouteSegmentsList) {
        return union(getSecondList(edgesAndRouteSegmentsList));
    }

    private static <A, B> List<A> getFirstList(final List<Pair<A, B>> pairList) {
        return pairList
                .stream()
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

    private static <A, B> List<B> getSecondList(final List<Pair<A, B>> pairList) {
        return pairList
                .stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());
    }
}
