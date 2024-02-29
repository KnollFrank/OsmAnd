package org.labyrinth.footpath.graph;

import static org.labyrinth.common.MeasureUtils.sum;

import com.google.common.collect.MoreCollectors;

import net.osmand.router.PostmanTourPlanner.RouteSegmentWithEquality;

import java.util.List;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Edges {

    public static Quantity<Length> getLength(final List<Edge> edges) {
        return sum(edges.stream().map(Edge::getLength));
    }

    public static Edge getEdgeContainingRouteSegment(final Set<Edge> edges, final RouteSegmentWithEquality routeSegment) {
        return edges
                .stream()
                .filter(edge -> edgeContainsRouteSegment(edge, routeSegment))
                .collect(MoreCollectors.onlyElement());
    }

    private static boolean edgeContainsRouteSegment(final Edge edge, final RouteSegmentWithEquality routeSegment) {
        return edge
                .routeSegments
                .stream()
                .map(RouteSegmentWithEquality::new)
                .anyMatch(routeSegment::equals);
    }
}
