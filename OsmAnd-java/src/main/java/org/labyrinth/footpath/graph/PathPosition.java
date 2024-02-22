package org.labyrinth.footpath.graph;

import static org.labyrinth.common.MeasureUtils.divide;
import static org.labyrinth.common.MeasureUtils.toMetres;

import org.labyrinth.coordinate.Geodetic;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class PathPosition {

    public final Path path;
    public final double fractionOfPathLength;

    public PathPosition(final Path path, final double fractionOfPathLength) {
        this.path = path;
        this.fractionOfPathLength = fractionOfPathLength;
    }

    public Geodetic getGeodetic() {
        return asEdgePosition().getGeodetic();
    }

    public EdgePosition asEdgePosition() {
        final Edge edge = path.getEdges().get(getEdgeIndex());
        return new EdgePosition(
                edge,
                divide(getLenOnCurrentEdge(), edge.getLength()));
    }

    public Quantity<Length> getCoveredPathLength() {
        return path.getLength().multiply(fractionOfPathLength);
    }

    private int currentEdgeIndex = -1;

    public int getEdgeIndex() {
        if (currentEdgeIndex == -1) {
            currentEdgeIndex = computeEdgeIndex();
        }
        return currentEdgeIndex;
    }

    private int computeEdgeIndex() {
        final List<Edge> edges = this.path.getEdges();
        final double lengthInMetres = toMetres(getCoveredPathLength());
        double actualLengthInMeters = 0;
        int edgeIndex = 0;
        while (actualLengthInMeters < lengthInMetres && edgeIndex < edges.size()) {
            actualLengthInMeters += toMetres(edges.get(edgeIndex).getLength());
            edgeIndex++;
        }
        return Math.max(edgeIndex - 1, 0);
    }

    private Quantity<Length> getLenOnCurrentEdge() {
        return getCoveredPathLength().subtract(getLenOfEdgesUpToCurrentEdge());
    }

    private Quantity<Length> getLenOfEdgesUpToCurrentEdge() {
        return Edges.getLength(path.getEdges().subList(0, getEdgeIndex()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PathPosition that = (PathPosition) o;
        return Double.compare(that.fractionOfPathLength, fractionOfPathLength) == 0 && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, fractionOfPathLength);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PathPosition.class.getSimpleName() + "[", "]")
                .add("path=" + path)
                .add("fractionOfPathLength=" + fractionOfPathLength)
                .toString();
    }
}
