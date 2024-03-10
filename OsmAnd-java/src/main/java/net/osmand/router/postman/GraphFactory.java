package net.osmand.router.postman;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.KILO;
import static tec.units.ri.unit.Units.METRE;

import net.osmand.router.BinaryRoutePlanner.RouteSegmentPoint;
import net.osmand.router.RoutingContext;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.common.MeasureUtils;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.coordinate.GeodeticFactory;
import org.labyrinth.footpath.converter.Circle;
import org.labyrinth.footpath.converter.ConnectedRouteSegmentsProvider;
import org.labyrinth.footpath.converter.ConnectedRouteSegmentsWithinAreaProvider;
import org.labyrinth.footpath.converter.RouteSegmentWithinCirclePredicate;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Edges;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;

class GraphFactory {

    public static Pair<Graph, Node> getGraphAndStartNode(final RoutingContext ctx, final RouteSegmentPoint start) {
        final Graph graph = getGraph(ctx, start);
        return Pair.of(graph, getNode(start, graph));
    }

    private static Graph getGraph(final RoutingContext ctx, final RouteSegmentPoint start) {
        return createGraphFactory(ctx, start).createGraph(new RouteSegmentWithEquality(start));
    }

    private static org.labyrinth.footpath.converter.GraphFactory createGraphFactory(final RoutingContext ctx, final RouteSegmentPoint start) {
        return new org.labyrinth.footpath.converter.GraphFactory(
                new ConnectedRouteSegmentsWithinAreaProvider(
                        new ConnectedRouteSegmentsProvider(ctx),
                        new RouteSegmentWithinCirclePredicate(
                                new Circle(
                                        GeodeticFactory.createGeodetic(start.getPreciseLatLon()),
                                        getQuantity(0.25, KILO(METRE))))));
    }

    private static Node getNode(final RouteSegmentPoint routeSegmentPoint, final Graph graph) {
        return getNode(
                routeSegmentPoint,
                Edges.getEdgeContainingRouteSegment(graph.edges, new RouteSegmentWithEquality(routeSegmentPoint)));
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
