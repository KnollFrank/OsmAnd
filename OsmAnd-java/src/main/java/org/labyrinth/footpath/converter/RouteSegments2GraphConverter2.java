package org.labyrinth.footpath.converter;

import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.PostmanTourPlanner.RouteSegmentWrapper;

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

public class RouteSegments2GraphConverter2 {

    private final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider;

    public RouteSegments2GraphConverter2(final IConnectedRouteSegmentsProvider connectedRouteSegmentsProvider) {
        this.connectedRouteSegmentsProvider = connectedRouteSegmentsProvider;
    }

    public Graph createGraph(final RouteSegmentWrapper startRouteSegment) {
//        final Set<Edge> edges = getEdges(routeSegments);
//        return new Graph(getNodes(edges), edges);
        return null;
    }

    private Set<Node> getNodes(final Set<Edge> edges) {
        return edges
                .stream()
                .flatMap(edge -> Stream.of(edge.source, edge.target))
                .collect(Collectors.toSet());
    }

    private Set<Edge> getEdges(final Set<RouteSegmentWrapper> routeSegments) {
        return routeSegments
                .stream()
                .map(routeSegmentWrapper -> routeSegmentWrapper.delegate)
                .map(routeSegment -> new Edge(getSourceNode(routeSegment), getTargetNode(routeSegment)))
                .collect(Collectors.toSet());
    }

    private Node getSourceNode(final RouteSegment routeSegment) {
        return new Node(
                new RoadPosition(routeSegment.getRoad().id, routeSegment.getSegmentStart()),
                getGeodetic(routeSegment, routeSegment.getSegmentStart()));
    }

    private Node getTargetNode(final RouteSegment routeSegment) {
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
