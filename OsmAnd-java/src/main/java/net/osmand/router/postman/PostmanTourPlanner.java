package net.osmand.router.postman;

import static net.osmand.router.BinaryRoutePlanner.FinalRouteSegment;
import static net.osmand.router.BinaryRoutePlanner.RouteSegment;
import static net.osmand.router.BinaryRoutePlanner.RouteSegmentPoint;

import net.osmand.router.RoutingContext;

import org.jgrapht.alg.util.Pair;
import org.labyrinth.common.ListUtils;
import org.labyrinth.common.Utils;
import org.labyrinth.footpath.core.ShortestClosedPathProvider;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class PostmanTourPlanner {

    private final RoutingContext routingContext;
    private final IPostmanTourPlannerProgress postmanTourPlannerProgress;

    public PostmanTourPlanner(final RoutingContext routingContext) {
        this.routingContext = routingContext;
        this.postmanTourPlannerProgress = new PostmanTourPlannerProgress(routingContext.calculationProgress);
    }

    public Optional<FinalRouteSegment> searchRoute(final RouteSegmentPoint start,
                                                   final Quantity<Length> radius) {
        this.routingContext.memoryOverhead = 1000;
        this.postmanTourPlannerProgress.searchRouteStarted();
        final Optional<FinalRouteSegment> finalRouteSegment =
                GraphFactory
                        .getGraphAndStartNode(
                                this.routingContext,
                                this.postmanTourPlannerProgress,
                                start,
                                radius)
                        .map(graphAndStartNode -> {
                            this.postmanTourPlannerProgress.getGraphAndStartNodeFinished();
                            return searchRoute(
                                    graphAndStartNode.getFirst(),
                                    graphAndStartNode.getSecond());
                        });
        this.postmanTourPlannerProgress.searchRouteFinished();
        return finalRouteSegment;
    }

    private static FinalRouteSegment searchRoute(final Graph graph, final Node startNode) {
        return path2StartOfConnectedRouteSegments(
                ShortestClosedPathProvider.createShortestClosedPathStartingAtNode(graph, startNode),
                graph);
    }

    private static FinalRouteSegment path2StartOfConnectedRouteSegments(final List<Node> path,
                                                                        final Graph graph) {
        return routeSegment2FinalRouteSegment(
                connectRouteSegmentsReturnStartOfChain(
                        path2RouteSegments(path, graph)));
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
                            actual.setParentRoute(previous);
                        });
    }

    private static List<RouteSegment> path2RouteSegments(final List<Node> path, final Graph graph) {
        return Utils
                .getConsecutivePairs(path)
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

    private static FinalRouteSegment routeSegment2FinalRouteSegment(final RouteSegment routeSegment) {
        final FinalRouteSegment finalRouteSegment =
                new FinalRouteSegment(
                        routeSegment.getRoad(),
                        routeSegment.getSegmentStart(),
                        routeSegment.getSegmentEnd());
        finalRouteSegment.setParentRoute(routeSegment.getParentRoute());
        return finalRouteSegment;
    }
}
