package org.labyrinth.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.interfaces.EulerianCycleAlgorithm;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.UnorderedPair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// adapted from org.jgrapht.alg.cycle.ChinesePostman
public class ChinesePostman<V, E> {

    public GraphPath<V, E> getCPPSolution(Graph<V, E> graph) {
        // Mixed graphs are currently not supported. Solving the CPP for mixed graphs is NP-Hard
        GraphTests.requireDirectedOrUndirected(graph);

        // If graph has no vertices, or no edges, instantly return.
        if (graph.vertexSet().isEmpty() || graph.edgeSet().isEmpty())
            return new HierholzerEulerianCycle<V, E>().getEulerianCycle(graph);

        assert GraphTests.isStronglyConnected(graph);

        if (graph.getType().isUndirected())
            return solveCPPUndirected(graph);
        else
            return solveCPPDirected(graph);
    }

    private GraphPath<V, E> solveCPPUndirected(Graph<V, E> graph) {

        // 1. Find all odd degree vertices (there should be an even number of those)
        List<V> oddDegreeVertices =
                graph.vertexSet().stream().filter(v -> graph.degreeOf(v) % 2 == 1).collect(
                        Collectors.toList());

        // 2. Compute all pairwise shortest paths for the oddDegreeVertices
        Map<Pair<V, V>, GraphPath<V, E>> shortestPaths = new HashMap<>();
        ShortestPathAlgorithm<V, E> sp = new DijkstraShortestPath<>(graph);
        for (int i = 0; i < oddDegreeVertices.size() - 1; i++) {
            V u = oddDegreeVertices.get(i);
            ShortestPathAlgorithm.SingleSourcePaths<V, E> paths = sp.getPaths(u);
            for (int j = i + 1; j < oddDegreeVertices.size(); j++) {
                V v = oddDegreeVertices.get(j);
                shortestPaths.put(new UnorderedPair<>(u, v), paths.getPath(v));
            }
        }

        // 3. Solve a matching problem. For that we create an auxiliary graph.
        Graph<V, DefaultWeightedEdge> auxGraph =
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(auxGraph, oddDegreeVertices);

        for (V u : oddDegreeVertices) {
            for (V v : oddDegreeVertices) {
                if (u == v)
                    continue;
                Graphs.addEdge(
                        auxGraph, u, v, shortestPaths.get(new UnorderedPair<>(u, v)).getWeight());
            }
        }
        MatchingAlgorithm.Matching<V, DefaultWeightedEdge> matching =
                new KolmogorovWeightedPerfectMatching<>(auxGraph).getMatching();

        // 4. On the original graph, add shortcuts between the odd vertices. These shortcuts have
        // been
        // identified by the matching algorithm. A shortcut from u to v
        // indirectly implies duplicating all edges on the shortest path from u to v
        Graph<V, E> eulerGraph = new Pseudograph<>(
                graph.getVertexSupplier(), graph.getEdgeSupplier(), graph.getType().isWeighted());
        Graphs.addGraph(eulerGraph, graph);
        Map<E, GraphPath<V, E>> shortcutEdges = new HashMap<>();
        for (DefaultWeightedEdge e : matching.getEdges()) {
            V u = auxGraph.getEdgeSource(e);
            V v = auxGraph.getEdgeTarget(e);
            E shortcutEdge = eulerGraph.addEdge(u, v);
            shortcutEdges.put(shortcutEdge, shortestPaths.get(new UnorderedPair<>(u, v)));
        }

        EulerianCycleAlgorithm<V, E> eulerianCycleAlgorithm = new HierholzerEulerianCycle<>();
        GraphPath<V, E> pathWithShortcuts = eulerianCycleAlgorithm.getEulerianCycle(eulerGraph);
        return replaceShortcutEdges(graph, pathWithShortcuts, shortcutEdges);
    }

    private GraphPath<V, E> solveCPPDirected(Graph<V, E> graph) {

        // 1. Find all imbalanced vertices
        Map<V, Integer> imbalancedVertices = new LinkedHashMap<>();
        Set<V> negImbalancedVertices = new HashSet<>();
        Set<V> postImbalancedVertices = new HashSet<>();
        for (V v : graph.vertexSet()) {
            int imbalance = graph.outDegreeOf(v) - graph.inDegreeOf(v);

            if (imbalance == 0)
                continue;
            imbalancedVertices.put(v, Math.abs(imbalance));

            if (imbalance < 0)
                negImbalancedVertices.add(v);
            else
                postImbalancedVertices.add(v);
        }

        // 2. Compute all pairwise shortest paths from the negative imbalanced vertices to the
        // positive imbalanced vertices
        Map<Pair<V, V>, GraphPath<V, E>> shortestPaths = new HashMap<>();
        ShortestPathAlgorithm<V, E> sp = new DijkstraShortestPath<>(graph);
        for (V u : negImbalancedVertices) {
            ShortestPathAlgorithm.SingleSourcePaths<V, E> paths = sp.getPaths(u);
            for (V v : postImbalancedVertices) {
                shortestPaths.put(new Pair<>(u, v), paths.getPath(v));
            }
        }

        // 3. Solve a matching problem. For that we create an auxiliary bipartite graph. Partition1
        // contains all nodes with negative imbalance,
        // Partition2 contains all nodes with positive imbalance. Each imbalanced node is duplicated
        // a number of times. The number of duplicates of a
        // node equals its imbalance.
        Graph<Integer, DefaultWeightedEdge> auxGraph =
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        List<V> duplicateMap = new ArrayList<>();
        Set<Integer> negImbalancedPartition = new HashSet<>();
        Set<Integer> postImbalancedPartition = new HashSet<>();
        Integer vertex = 0;

        for (V v : negImbalancedVertices) {
            for (int i = 0; i < imbalancedVertices.get(v); i++) {
                auxGraph.addVertex(vertex);
                duplicateMap.add(v);
                negImbalancedPartition.add(vertex);
                vertex++;
            }
        }
        for (V v : postImbalancedVertices) {
            for (int i = 0; i < imbalancedVertices.get(v); i++) {
                auxGraph.addVertex(vertex);
                duplicateMap.add(v);
                postImbalancedPartition.add(vertex);
                vertex++;
            }
        }

        for (Integer i : negImbalancedPartition) {
            for (Integer j : postImbalancedPartition) {
                V u = duplicateMap.get(i);
                V v = duplicateMap.get(j);
                Graphs.addEdge(auxGraph, i, j, shortestPaths.get(new Pair<>(u, v)).getWeight());
            }
        }
        MatchingAlgorithm.Matching<Integer, DefaultWeightedEdge> matching =
                new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(
                        auxGraph, negImbalancedPartition, postImbalancedPartition).getMatching();

        // 4. On the original graph, add shortcuts between the imbalanced vertices. These shortcuts
        // have
        // been identified by the matching algorithm. A shortcut from u to v
        // indirectly implies duplicating all edges on the shortest path from u to v

        Graph<V, E> eulerGraph = new DirectedPseudograph<>(
                graph.getVertexSupplier(), graph.getEdgeSupplier(), graph.getType().isWeighted());
        Graphs.addGraph(eulerGraph, graph);
        Map<E, GraphPath<V, E>> shortcutEdges = new HashMap<>();
        for (DefaultWeightedEdge e : matching.getEdges()) {
            int i = auxGraph.getEdgeSource(e);
            int j = auxGraph.getEdgeTarget(e);
            V u = duplicateMap.get(i);
            V v = duplicateMap.get(j);
            E shortcutEdge = eulerGraph.addEdge(u, v);
            shortcutEdges.put(shortcutEdge, shortestPaths.get(new Pair<>(u, v)));
        }

        EulerianCycleAlgorithm<V, E> eulerianCycleAlgorithm = new HierholzerEulerianCycle<>();
        GraphPath<V, E> pathWithShortcuts = eulerianCycleAlgorithm.getEulerianCycle(eulerGraph);

        return replaceShortcutEdges(graph, pathWithShortcuts, shortcutEdges);
    }

    private GraphPath<V, E> replaceShortcutEdges(
            Graph<V, E> inputGraph, GraphPath<V, E> pathWithShortcuts,
            Map<E, GraphPath<V, E>> shortcutEdges) {
        V startVertex = pathWithShortcuts.getStartVertex();
        V endVertex = pathWithShortcuts.getEndVertex();
        List<V> vertexList = new ArrayList<>();
        List<E> edgeList = new ArrayList<>();

        List<V> verticesInPathWithShortcuts = pathWithShortcuts.getVertexList(); // should contain
        // at least 2
        // vertices
        List<E> edgesInPathWithShortcuts = pathWithShortcuts.getEdgeList(); // cannot be empty
        for (int i = 0; i < verticesInPathWithShortcuts.size() - 1; i++) {
            vertexList.add(verticesInPathWithShortcuts.get(i));
            E edge = edgesInPathWithShortcuts.get(i);

            if (shortcutEdges.containsKey(edge)) { // shortcut edge
                // replace shortcut edge by its implied path
                GraphPath<V, E> shortcut = shortcutEdges.get(edge);
                if (vertexList.get(vertexList.size() - 1).equals(shortcut.getStartVertex())) { // check
                    // direction
                    // of
                    // path
                    vertexList.addAll(
                            shortcut.getVertexList().subList(1, shortcut.getVertexList().size() - 1));
                    edgeList.addAll(shortcut.getEdgeList());
                } else {
                    List<V> reverseVertices = new ArrayList<>(
                            shortcut.getVertexList().subList(1, shortcut.getVertexList().size() - 1));
                    Collections.reverse(reverseVertices);
                    List<E> reverseEdges = new ArrayList<>(shortcut.getEdgeList());
                    Collections.reverse(reverseEdges);
                    vertexList.addAll(reverseVertices);
                    edgeList.addAll(reverseEdges);
                }
            } else { // original edge
                edgeList.add(edge);
            }
        }
        vertexList.add(endVertex);
        double pathWeight = edgeList.stream().mapToDouble(inputGraph::getEdgeWeight).sum();

        return new GraphWalk<>(
                inputGraph, startVertex, endVertex, vertexList, edgeList, pathWeight);
    }
}
