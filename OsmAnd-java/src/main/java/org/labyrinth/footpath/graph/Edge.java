package org.labyrinth.footpath.graph;

import com.google.common.collect.Lists;

import net.osmand.router.BinaryRoutePlanner.RouteSegment;

import org.labyrinth.coordinate.Angle;

import java.util.List;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Edge {

    public final Node source;
    public final Node target;
    public final List<RouteSegment> routeSegments;
    private final Quantity<Length> length;
    private final Angle bearing;

    public Edge(final Node source, final Node target, final List<RouteSegment> routeSegments) {
        this.source = source;
        this.target = target;
        this.routeSegments = routeSegments;
        // FK-TODO: Nach Performancemessung vielleicht length und bearing nicht sofort berechnen, sondern in den getter-Methoden getLength() und getCompDir() cachen. Dann auch in toString() diese Getter-Methoden verwenden anstelle der entsprechenden Instanzvariablen.
        this.length = source.getDistanceTo(target);
        this.bearing = source.getBearing(target);
    }

    public Angle getDirection() {
        return bearing;
    }

    public Quantity<Length> getLength() {
        return length;
    }

    public boolean containsNodes(final Node a, final Node b) {
        return isSource2Target(a, b) || isSource2Target(b, a);
    }

    public Edge reverse() {
        return new Edge(target, source, Lists.reverse(routeSegments));
    }

    private boolean isSource2Target(final Node source, final Node target) {
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
        ret += "\n    Length: " + length;
        ret += "\n    Bearing: " + bearing;
        return ret;
    }
}
