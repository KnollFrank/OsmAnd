package org.labyrinth.footpath.graph;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;

import org.labyrinth.common.ListUtils;
import org.labyrinth.coordinate.Angle;

import java.util.List;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Edge {

    public final Node source;
    public final Node target;
    public final List<RouteSegment> routeSegments;
    private Quantity<Length> length;
    private Angle bearing;

    public Edge(final Node source, final Node target, final List<RouteSegment> routeSegments) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(source + " = " + target);
        }
        this.source = source;
        this.target = target;
        this.routeSegments = routeSegments;
    }

    public Angle getDirection() {
        if (bearing == null) {
            bearing = source.getBearing(target);
        }
        return bearing;
    }

    public Quantity<Length> getLength() {
        if (length == null) {
            length = source.getDistanceTo(target);
        }
        return length;
    }

    public boolean containsNodes(final Node a, final Node b) {
        return isSource2Target(a, b) || isSource2Target(b, a);
    }

    public Edge reverse() {
        return new Edge(target, source, reverse(routeSegments));
    }

    public boolean isSource2Target(final Node source, final Node target) {
        return this.source.equals(source) && this.target.equals(target);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Edge edge = (Edge) o;
        return source.equals(edge.source) &&
                target.equals(edge.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

    @Override
    public String toString() {
        String ret = "\nEdge(" + source.id + " to " + target.id + "): ";
        ret += "\n    Length: " + getLength();
        ret += "\n    Bearing: " + getDirection();
        return ret;
    }

    private static List<RouteSegment> reverse(final List<RouteSegment> routeSegments) {
        return routeSegments
                .stream()
                .map(Edge::reverse)
                .collect(ListUtils.toReversedList());
    }

    private static RouteSegment reverse(final RouteSegment routeSegment) {
        final short segmentStart = routeSegment.getSegmentEnd();
        final short segmentEnd = routeSegment.getSegmentStart();
        return new RouteSegment(routeSegment.getRoad(), segmentStart, segmentEnd);
    }
}
