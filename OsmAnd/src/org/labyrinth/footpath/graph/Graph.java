package org.labyrinth.footpath.graph;

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
            edge.getSource().getLocEdges().add(edge);
            edge.getTarget().getLocEdges().add(edge);
        }
    }
}
