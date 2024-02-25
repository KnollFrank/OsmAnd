package org.labyrinth.footpath.graph;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphFactory {

    public static Graph createGraph(final Set<Edge> edges) {
        final Set<Node> nodes = getNodes(edges);
        return new Graph(nodes, normalizeEdges(edges, nodes));
    }

    private static Set<Node> getNodes(final Set<Edge> edges) {
        return edges
                .stream()
                .flatMap(edge -> Stream.of(edge.source, edge.target))
                .collect(Collectors.toSet());
    }

    private static Set<Edge> normalizeEdges(final Set<Edge> edges, final Set<Node> nodes) {
        return edges
                .stream()
                .map(edge -> normalizeEdge(edge, nodes))
                .collect(Collectors.toSet());
    }

    private static Edge normalizeEdge(final Edge edge, final Set<Node> nodes) {
        return new Edge(
                getNodeById(edge.source.id, nodes),
                getNodeById(edge.target.id, nodes),
                edge.routeSegments);
    }

    private static Node getNodeById(final EquivalentRoadPositions id, final Set<Node> nodes) {
        return nodes
                .stream()
                .filter(node -> node.id.equals(id))
                .findFirst()
                .get();
    }
}
