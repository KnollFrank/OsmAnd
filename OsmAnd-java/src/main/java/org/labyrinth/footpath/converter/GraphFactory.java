package org.labyrinth.footpath.converter;

import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.PostmanTourPlanner.RouteSegmentWrapper;
import static net.osmand.router.PostmanTourPlanner.isSameRoad;

import com.google.common.collect.ImmutableSet;

import net.osmand.binary.RouteDataObject;
import net.osmand.util.MapUtils;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphFactory {

    private final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider;

    public GraphFactory(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider) {
        this.connectedRouteSegmentsProvider = connectedRouteSegmentsProvider;
    }

    public Graph createGraph(final RouteSegmentWrapper start) {
        final Set<RouteSegmentWrapper> routeSegmentsWithoutStart = getRouteSegmentsWithoutStart(start);
        final Set<Edge> edges =
                ImmutableSet
                        .<Edge>builder()
                        .add(asEdge(start.delegate))
                        .addAll(asEdges(routeSegmentsWithoutStart))
                        .addAll(getStart2OtherRoad(routeSegmentsWithoutStart, start))
                        .build();
        return new Graph(getNodes(edges), edges);
    }

    private Set<RouteSegmentWrapper> getRouteSegmentsWithoutStart(final RouteSegmentWrapper start) {
        return connectedRouteSegmentsProvider
                // FK-TODO: ev. getConnectedRouteSegments() ohne start zurückliefern?
                .getConnectedRouteSegments(start)
                .stream()
                .filter(routeSegmentWrapper -> !routeSegmentWrapper.equals(start))
                .collect(Collectors.toSet());
    }

    private static Set<Edge> getStart2OtherRoad(final Set<RouteSegmentWrapper> routeSegments,
                                                final RouteSegmentWrapper start) {
        final Node startTarget = getTargetNode(start.delegate);
        return routeSegments
                .stream()
                .filter(routeSegment -> !isSameRoad(routeSegment, start))
                .map(routeSegmentFromOtherRoad ->
                        new Edge(
                                startTarget,
                                getSourceNode(routeSegmentFromOtherRoad.delegate)))
                .collect(Collectors.toSet());
    }

    private Set<Node> getNodes(final Set<Edge> edges) {
        return edges
                .stream()
                .flatMap(edge -> Stream.of(edge.source, edge.target))
                .collect(Collectors.toSet());
    }

    private static Set<Edge> asEdges(final Set<RouteSegmentWrapper> routeSegments) {
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
}
