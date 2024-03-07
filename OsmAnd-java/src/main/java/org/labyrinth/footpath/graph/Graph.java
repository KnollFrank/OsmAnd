package org.labyrinth.footpath.graph;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Graph {

    public final Set<Node> nodes;
    public final Set<Edge> edges;

    Graph(final Set<Node> nodes, final Set<Edge> edges) {
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

    public Optional<Edge> findEdgeContainingNodes(final Node a, final Node b) {
        return a
                .locEdges
                .stream()
                .filter(edge -> edge.containsNodes(a, b))
                .findFirst();
    }

    public Optional<Edge> findEdgeFromSource2Target(final Node source, final Node target) {
        return source
                .locEdges
                .stream()
                .filter(edge -> edge.isSource2Target(source, target))
                .findFirst();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Graph graph = (Graph) o;
        return Objects.equals(nodes, graph.nodes) && Objects.equals(edges, graph.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, edges);
    }
}
