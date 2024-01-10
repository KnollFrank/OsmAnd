package org.labyrinth.footpath.core;

import com.google.common.collect.ImmutableList;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EdgePosition;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.Path;
import org.labyrinth.footpath.graph.PathFactory;
import org.labyrinth.common.Pair;
import org.labyrinth.footpath.graph.PathPosition;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Collections;
import java.util.List;

import static org.labyrinth.common.ListUtils.isValidIndex;
import static org.labyrinth.common.MeasureUtils.divide;
import static org.labyrinth.common.MeasureUtils.isGreaterThan;

class SubpathProvider {

    private final List<Edge> edges;

    public static Path getSubpath(final PathPosition start, final Quantity<Length> length) {
        return new SubpathProvider(start.path.getEdges())._getSubpath(start, length);
    }

    private SubpathProvider(final List<Edge> edges) {
        this.edges = edges;
    }

    private Path _getSubpath(final PathPosition start, final Quantity<Length> length) {
        final Pair<EdgePosition, List<Node>> endPosition_intermediateNodes =
                get_endPosition_intermediateNodes(
                        start.asEdgePosition(),
                        start.getEdgeIndex(),
                        length,
                        Collections.emptyList());
        return PathFactory.createPath(
                start.asEdgePosition(),
                endPosition_intermediateNodes.second,
                endPosition_intermediateNodes.first);
    }

    private Pair<EdgePosition, List<Node>> get_endPosition_intermediateNodes(
            final EdgePosition edgePosition,
            final int edgeIndex,
            final Quantity<Length> length,
            final List<Node> intermediateNodes) {

        if (isGreaterThan(edgePosition.getRestLength(), length)) {
            return Pair.create(
                    add(edgePosition, length),
                    intermediateNodes);
        }

        final int nextEdgeIndex = edgeIndex + 1;
        if (!isValidIndex(this.edges, nextEdgeIndex)) {
            return Pair.create(
                    new EdgePosition(edgePosition.edge, 1),
                    intermediateNodes);
        }

        final Edge nextEdge = this.edges.get(nextEdgeIndex);
        return get_endPosition_intermediateNodes(
                new EdgePosition(nextEdge, 0),
                nextEdgeIndex,
                length.subtract(edgePosition.getRestLength()),
                ImmutableList
                        .<Node>builder()
                        .addAll(intermediateNodes)
                        .add(nextEdge.getSource())
                        .build());
    }

    private static EdgePosition add(final EdgePosition edgePosition, final Quantity<Length> length) {
        return new EdgePosition(
                edgePosition.edge,
                divide(
                        edgePosition.getCoveredLength().add(length),
                        edgePosition.edge.getLength()));
    }
}
