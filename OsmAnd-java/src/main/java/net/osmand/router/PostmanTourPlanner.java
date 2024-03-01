package net.osmand.router;

import static net.osmand.router.BinaryRoutePlanner.FinalRouteSegment;
import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.BinaryRoutePlanner.RouteSegmentPoint;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.common.ListUtils;
import org.labyrinth.common.MeasureUtils;
import org.labyrinth.common.Utils;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.coordinate.GeodeticFactory;
import org.labyrinth.footpath.converter.ConnectedRouteSegmentsProvider;
import org.labyrinth.footpath.converter.GraphFactory;
import org.labyrinth.footpath.core.ShortestClosedPathProvider;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Edges;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostmanTourPlanner {

    public FinalRouteSegment searchRoute(final RoutingContext ctx,
                                         final RouteSegmentPoint start) {
        ctx.memoryOverhead = 1000;
        final Graph graph = getGraph(ctx, start);
        final List<Node> shortestClosedPath =
                ShortestClosedPathProvider.createShortestClosedPathStartingAtNode(
                        graph,
                        getNode(start, graph));
        return getFinalRouteSegment(shortestClosedPath, graph);
    }

    private static Graph getGraph(final RoutingContext ctx, final RouteSegmentPoint start) {
        final GraphFactory graphFactory = new GraphFactory(new ConnectedRouteSegmentsProvider(ctx));
        return graphFactory.createGraph(new RouteSegmentWithEquality(start));
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

    private static FinalRouteSegment getFinalRouteSegment(final List<Node> shortestClosedPath, final Graph graph) {
        final List<RouteSegment> routeSegments = getRouteSegments(shortestClosedPath, graph);
        final RouteSegment routeSegment = connectRouteSegmentsReturnStartOfChain(routeSegments);
        return createFinalRouteSegment(routeSegment);
    }

    private static RouteSegment connectRouteSegmentsReturnStartOfChain(final List<RouteSegment> routeSegments) {
        connectRouteSegments(routeSegments);
        return ListUtils.getEnd(routeSegments).get();
    }

    private static void connectRouteSegments(final List<RouteSegment> routeSegments) {
        Utils
                .getConsecutivePairs(routeSegments)
                .forEach(
                        previous_actual_pair -> {
                            final RouteSegment previous = previous_actual_pair.getFirst();
                            final RouteSegment actual = previous_actual_pair.getSecond();
                            if (actual.getParentRoute() != null) {
                                // FK-TODO: remove:
                                System.out.println("oh, je");
                            }
                            actual.setParentRoute(previous);
                        });
    }

    private static List<RouteSegment> getRouteSegments(final List<Node> shortestClosedPath, final Graph graph) {
        return Utils
                .getConsecutivePairs(shortestClosedPath)
                .map(sourceTargetPair -> getEdgeFromSource2Target(graph, sourceTargetPair))
                .flatMap(edge -> edge.routeSegments.stream())
                .map(PostmanTourPlanner::copy)
                .collect(Collectors.toList());
    }

    private static RouteSegment copy(final RouteSegment routeSegment) {
        return new RouteSegment(
                routeSegment.getRoad(),
                routeSegment.getSegmentStart(),
                routeSegment.getSegmentEnd());
    }

    private static Edge getEdgeFromSource2Target(final Graph graph,
                                                 final Pair<Node, Node> sourceTargetPair) {
        return graph
                .findEdgeFromSource2Target(
                        sourceTargetPair.getFirst(),
                        sourceTargetPair.getSecond())
                .orElseThrow(() -> new NoSuchElementException(String.valueOf(sourceTargetPair)));
    }

    private static FinalRouteSegment createFinalRouteSegment(final RouteSegment routeSegment) {
        final FinalRouteSegment finalRouteSegment =
                new FinalRouteSegment(
                        routeSegment.getRoad(),
                        routeSegment.getSegmentStart(),
                        routeSegment.getSegmentEnd());
        finalRouteSegment.setParentRoute(routeSegment.getParentRoute());
        return finalRouteSegment;
    }

    public static class RouteSegmentWithEquality {

        public final RouteSegment delegate;

        public RouteSegmentWithEquality(final RouteSegment delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final RouteSegmentWithEquality that = (RouteSegmentWithEquality) o;
            return this.delegate.getRoad().id == that.delegate.getRoad().id &&
                    this.delegate.getSegmentStart() == that.delegate.getSegmentStart() &&
                    this.delegate.getSegmentEnd() == that.delegate.getSegmentEnd();
        }

        @Override
        public int hashCode() {
            return Objects.hash(delegate.getRoad().id, delegate.getSegmentStart(), delegate.getSegmentEnd());
        }

        @Override
        public String toString() {
            return "RouteSegmentWrapper{" +
                    "delegate=" + delegate +
                    '}';
        }
    }
}
