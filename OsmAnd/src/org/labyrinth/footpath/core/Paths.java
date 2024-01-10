package org.labyrinth.footpath.core;

import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.Path;
import org.labyrinth.footpath.graph.PathFactory;
import org.labyrinth.footpath.graph.PathPosition;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;
import java.util.stream.Collectors;

import static org.labyrinth.common.ListUtils.getSublist;

public class Paths {

    public static Path getSubpath(final PathPosition start, final Quantity<Length> length) {
        return SubpathProvider.getSubpath(start, length);
    }

    public static Path getSubpathStartingAt(final PathPosition pathPosition) {
        return PathFactory.createPath(
                pathPosition.asEdgePosition(),
                getTargets(getSublist(pathPosition.path.getEdges(), pathPosition.getEdgeIndex())),
                pathPosition.path.dst);
    }

    private static List<Node> getTargets(final List<Edge> edges) {
        return edges
                .stream()
                .map(Edge::getTarget)
                .collect(Collectors.toList());
    }
}
