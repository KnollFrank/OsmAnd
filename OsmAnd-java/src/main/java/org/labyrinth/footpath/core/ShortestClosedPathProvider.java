package org.labyrinth.footpath.core;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.labyrinth.footpath.converter.Graph2JGraphConverter;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.jgrapht.alg.cycle.ChinesePostman;

import java.util.List;

public class ShortestClosedPathProvider {

    public static List<Node> createShortestClosedPathStartingAtNode(final Graph graph, final Node startOfPath) {
        return ClosedPaths.getClosedPathStartingAtNode(
                getShortestClosedPathContainingNode(graph, startOfPath),
                startOfPath);
    }

    private static List<Node> getShortestClosedPathContainingNode(final Graph graph, final Node node) {
        return getShortestClosedPathContainingNode(
                Graph2JGraphConverter.convert(graph),
                node);
    }

    private static List<Node>  getShortestClosedPathContainingNode(
            final org.jgrapht.Graph<Node, DefaultWeightedEdge> graph,
            final Node node) {
        return getShortestClosedPath(getSubgraphContainingNode(graph, node));
    }

    private static List<Node> getShortestClosedPath(final org.jgrapht.Graph<Node, DefaultWeightedEdge> graph) {
        return new ChinesePostman<Node, DefaultWeightedEdge>()
                .getCPPSolution(graph)
                .getVertexList();
    }

    private static org.jgrapht.Graph<Node, DefaultWeightedEdge> getSubgraphContainingNode(
            final org.jgrapht.Graph<Node, DefaultWeightedEdge> graph,
            final Node node) {
        return new AsSubgraph<>(
                graph,
                new ConnectivityInspector<>(graph).connectedSetOf(node));
    }
}
