package org.labyrinth.footpath.core;

import org.jgrapht.alg.cycle.ChinesePostman;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.labyrinth.common.Utils;
import org.labyrinth.footpath.converter.FootpathGraph2JGraphConverter;
import org.labyrinth.footpath.graph.Graph;
import org.labyrinth.footpath.graph.Node;

import java.util.List;
import java.util.stream.Collectors;

public class ShortestClosedPathProvider {

    public static List<Node> createShortestClosedPathStartingAtNode(final Graph graph, final Node startOfPath) {
        return ClosedPaths.getClosedPathStartingAtNode(
                getShortestClosedPathContainingNode(graph, startOfPath),
                startOfPath);
    }

    private static List<Node> getShortestClosedPathContainingNode(final Graph graph, final Node node) {
        return getShortestClosedPathContainingNode(
                new FootpathGraph2JGraphConverter().convert(graph),
                node);
    }

    private static List<Node> getShortestClosedPathContainingNode(
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
                Utils
                        .asStream(new DepthFirstIterator<>(graph, node))
                        .collect(Collectors.toSet()));
    }
}
