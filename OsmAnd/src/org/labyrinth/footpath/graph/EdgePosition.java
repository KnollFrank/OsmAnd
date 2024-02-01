package org.labyrinth.footpath.graph;

import org.labyrinth.coordinate.Geodetic;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class EdgePosition {

    public final Edge edge;
    public final double fractionOfEdgeLength;

    public EdgePosition(final Edge edge, final double fractionOfEdgeLength) {
        this.edge = edge;
        this.fractionOfEdgeLength = fractionOfEdgeLength;
    }

    public Optional<Node> getNode() {
        if (fractionOfEdgeLength == 0.0) {
            return Optional.of(edge.getSource());
        }
        if (fractionOfEdgeLength == 1.0) {
            return Optional.of(edge.getTarget());
        }
        return Optional.empty();
    }

    public Geodetic getGeodetic() {
        return edge.getSource().getPosition().moveIntoDirection(edge.getTarget().getPosition(), fractionOfEdgeLength);
    }

    public boolean almostEquals(final EdgePosition other, final double eps) {
        return this.edge.equals(other.edge) && Math.abs(fractionOfEdgeLength - other.fractionOfEdgeLength) < eps;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EdgePosition edgePosition = (EdgePosition) o;
        return Double.compare(edgePosition.fractionOfEdgeLength, fractionOfEdgeLength) == 0 &&
                edge.equals(edgePosition.edge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge, fractionOfEdgeLength);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EdgePosition.class.getSimpleName() + "[", "]")
                .add("edge=" + edge)
                .add("fractionOfEdgeLength=" + fractionOfEdgeLength)
                .toString();
    }
}
