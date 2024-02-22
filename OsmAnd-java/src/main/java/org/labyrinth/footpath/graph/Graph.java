package org.labyrinth.footpath.graph;

import java.util.Optional;
import java.util.Set;

public class Graph {

    public final Set<Node> nodes;
    public final Set<Edge> edges;

    public Graph(final Set<Node> nodes, final Set<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        addLocEdgesToNodes();
    }

    private void addLocEdgesToNodes() {
        for (final Edge edge : edges) {
            edge.source.locEdges.add(edge);
            edge.target.locEdges.add(edge);
        }
    }

    public Optional<Node> findNodeById(final long id) {
        return this
                .nodes
                .stream()
                .filter(node -> node.id == id)
                .findFirst();
    }

    public Optional<Edge> findEdgeContainingNodes(final Node a, final Node b) {
        return a
                .locEdges
                .stream()
                .filter(edge -> edge.containsNodes(a, b))
                .findFirst();
    }

    public Node getNodeByName(final String name) {
        return
                this
                        .nodes
                        .stream()
                        .filter(node -> node.name != null && node.name.equals(name))
                        .findFirst()
                        .get();
    }
}
