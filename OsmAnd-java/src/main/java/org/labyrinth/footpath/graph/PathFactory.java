package org.labyrinth.footpath.graph;

import org.labyrinth.common.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PathFactory {

    public static Optional<Path> createPath(final List<Node> nodes) {
        if (nodes.size() < 2) {
            return Optional.empty();
        }
        final Path path =
                createPath(
                        new EdgePosition(
                                new Edge(
                                        nodes.get(0),
                                        nodes.get(1),
                                        Collections.emptyList()),
                                0),
                        nodes,
                        new EdgePosition(
                                new Edge(
                                        nodes.get(nodes.size() - 2),
                                        nodes.get(nodes.size() - 1),
                                        Collections.emptyList()),
                                1));
        return Optional.of(path);
    }

    public static Path createPath(final EdgePosition src,
                                  final List<Node> intermediateNodes,
                                  final EdgePosition dst) {
        return new Path(
                src,
                getNormalizedIntermediateNodes(src, intermediateNodes, dst),
                dst);
    }

    private static List<Node> getNormalizedIntermediateNodes(final EdgePosition src,
                                                             final List<Node> intermediateNodes,
                                                             final EdgePosition dst) {
        final List<Node> finalIntermediateNodes = new ArrayList<>(intermediateNodes);
        removePositionFromNodesIfEqualsAtIndex(src, finalIntermediateNodes, 0);
        removePositionFromNodesIfEqualsAtIndex(dst, finalIntermediateNodes, finalIntermediateNodes.size() - 1);
        return finalIntermediateNodes;
    }

    private static void removePositionFromNodesIfEqualsAtIndex(final EdgePosition edgePosition,
                                                               final List<Node> nodes,
                                                               final int index) {
        if (positionEqualsNodesAtIndex(edgePosition, nodes, index)) {
            nodes.remove(index);
        }
    }

    private static boolean positionEqualsNodesAtIndex(final EdgePosition edgePosition, final List<Node> nodes, final int index) {
        return ListUtils.isValidIndex(nodes, index) && edgePosition.getNode().equals(Optional.of(nodes.get(index)));
    }
}
