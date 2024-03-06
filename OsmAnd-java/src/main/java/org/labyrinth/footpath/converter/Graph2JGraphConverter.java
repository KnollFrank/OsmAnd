package org.labyrinth.footpath.converter;

import static org.labyrinth.common.MeasureUtils.toMetres;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;

public class Graph2JGraphConverter {

    public static org.jgrapht.Graph<Node, DefaultWeightedEdge> convert(final Graph graph) {
        final org.jgrapht.Graph<Node, DefaultWeightedEdge> jgraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        for (final Edge edge : graph.edges) {
            Graphs.addEdgeWithVertices(
                    jgraph,
                    edge.source,
                    edge.target,
                    toMetres(edge.getLength()));
        }
        return jgraph;
    }
}
