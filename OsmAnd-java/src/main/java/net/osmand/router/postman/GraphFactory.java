package net.osmand.router.postman;

import net.osmand.router.BinaryRoutePlanner.RouteSegmentPoint;
import net.osmand.router.RoutingContext;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.common.MeasureUtils;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.coordinate.GeodeticFactory;
import org.labyrinth.footpath.converter.Circle;
import org.labyrinth.footpath.converter.ConnectedRouteSegmentsProvider;
import org.labyrinth.footpath.converter.ConnectedRouteSegmentsWithinAreaProvider;
import org.labyrinth.footpath.converter.RouteSegmentPartlyWithinCirclePredicate;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Edges;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;

import java.util.Optional;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

class GraphFactory {

    public static Optional<Pair<Graph, Node>> getGraphAndStartNode(final RoutingContext ctx,
                                                                   final RouteSegmentPoint start,
                                                                   final Quantity<Length> radius) {
        final Graph graph = getGraph(ctx, start, radius);
        return getNode(start, graph).map(startNode -> Pair.of(graph, startNode));
    }

    private static Graph getGraph(final RoutingContext ctx,
                                  final RouteSegmentPoint start,
                                  final Quantity<Length> radius) {
        return createGraphFactory(ctx, start, radius).createGraph(new RouteSegmentWithEquality(start));
    }

    private static org.labyrinth.footpath.converter.GraphFactory createGraphFactory(
            final RoutingContext ctx,
            final RouteSegmentPoint start,
            final Quantity<Length> radius) {
        return new org.labyrinth.footpath.converter.GraphFactory(
                new ConnectedRouteSegmentsWithinAreaProvider(
                        new ConnectedRouteSegmentsProvider(ctx),
                        new RouteSegmentPartlyWithinCirclePredicate(
                                new Circle(
                                        GeodeticFactory.createGeodetic(start),
                                        radius))));
    }

    private static Optional<Node> getNode(final RouteSegmentPoint routeSegmentPoint, final Graph graph) {
        return Edges
                .getEdgeContainingRouteSegment(
                        graph.edges,
                        new RouteSegmentWithEquality(routeSegmentPoint))
                .map(edge -> getNode(routeSegmentPoint, edge));
    }

    private static Node getNode(final RouteSegmentPoint needle, final Edge haystack) {
        return getNode(GeodeticFactory.createGeodetic(needle.getPreciseLatLon()), haystack);
    }

    private static Node getNode(final Geodetic needle, final Edge haystack) {
        return matchesSourceNode(needle, haystack) ? haystack.source : haystack.target;
    }

    private static boolean matchesSourceNode(final Geodetic geodetic, final Edge edge) {
        return MeasureUtils.isLessOrEqual(
                geodetic.getDistanceTo(edge.source.position),
                geodetic.getDistanceTo(edge.target.position));
    }
}
