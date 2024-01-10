package org.labyrinth.converter;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;

import static org.labyrinth.common.MeasureUtils.toMeters;

public class FootpathGraph2JGraphConverter
{

    public org.jgrapht.Graph<Node, DefaultWeightedEdge> convert(final Graph graph) {
        final org.jgrapht.Graph<Node, DefaultWeightedEdge> jgraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        for (final Edge edge : graph.edges) {
            Graphs.addEdgeWithVertices(
                    jgraph,
                    edge.getSource(),
                    edge.getTarget(),
                    toMeters(edge.getLength()));
        }
        return jgraph;
    }
}
