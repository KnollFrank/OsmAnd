package org.labyrinth.footpath.core;

import android.util.Log;

import com.google.common.collect.ImmutableList;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EdgePosition;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.NodeBuilder;
import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Unit;
import org.labyrinth.footpath.graph.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.labyrinth.common.Utils.getConsecutivePairs;

public class SimplifiedEdgesProvider {

    private final boolean log;

    public SimplifiedEdgesProvider(final boolean log) {
        this.log = log;
    }

    public List<Edge> getSimplifiedEdges(final Path path) {
        return getSimplifiedEdges(getNodes(path));
    }

    private List<Node> getNodes(final Path path) {
        return ImmutableList
                .<Node>builder()
                .add(getNode(path.src, 1, "src"))
                .addAll(path.intermediateNodes)
                .add(getNode(path.dst, 2, "dst"))
                .build();
    }

    private Node getNode(final EdgePosition edgePosition, final int newid, final String newName) {
        return edgePosition
                .getNode()
                .orElseGet(() ->
                        new NodeBuilder()
                                .withId(newid)
                                .withPosition(edgePosition.getGeodetic())
                                .withName(newName)
                                .createNode());
    }

    // FK-TODO: refactor
    private List<Edge> getSimplifiedEdges(final List<Node> path) {
        final List<Edge> tempEdges =
                getConsecutivePairs(path)
                        .map(sourceTargetPair -> new Edge(sourceTargetPair.first, sourceTargetPair.second))
                        .collect(Collectors.toList());

        if (log) {
            Log.i("FOOTPATH", "Number of edges before merge: " + tempEdges.size());
        }

        // Now that we have the correct order of nodes, and initial bearings of edges
        // we look for successive edges with little difference in their bearing
        // to simplify the path, having less but longer edges

        // Allow a difference of diff degrees to both sides
        final Angle diff = new Angle(8.0, Unit.DEGREES);
        final List<Edge> simplifiedEdgesOfShortestPath = new LinkedList<>();
        // This will be the last node of the last edge equaling edge_i
        Node node_x_1 = null;

        // Data to sum up for needed merge;
        int last_i = -1;
        // Iterate over all edges
        for (int i = 0; i < tempEdges.size(); i++) {
            // The current edge to find equaling edges to
            final Edge edge_i = tempEdges.get(i);
            // The first node of current edge
            final Node node_i_0 = tempEdges.get(i).getSource();
            if (log) {
                Log.i("FOOTPATH", "Edge (" + (i + 1) + "/" + tempEdges.size() + ") dir: " + edge_i.getDirection());
            }
            last_i = i;
            for (int j = i + 1; j < tempEdges.size(); j++) {
                final Edge edge_j = tempEdges.get(j);
                // Only merge edges if they are identical in their characteristics
                if (equals(edge_i, edge_j, diff)) {
                    if (log) {
                        Log.i("FOOTPATH", "Adding " + edge_j.getDirection());
                    }
                    // Edge_i and edge_j can be merged
                    // Save last node1 of last edge_j equaling edge_i
                    node_x_1 = edge_j.getTarget();
                } else {
                    if (log) {
                        Log.i("FOOTPATH", "Not Merging " + edge_j.getDirection());
                    }
                    // Edge_i and edge_j can not be merged
                    // Merge possible previously found edges and add them

                    // Point to latest edge to try matching from
                    i = j - 1;

                    // Nothing can be merged, leave edge_i as is
                    if (node_x_1 == null) {
                        if (log) {
                            Log.i("FOOTPATH", "Created same edge i " + edge_i.getLength() + " and direction " + edge_i.getDirection());
                        }
                        // Add same edge_i
                        simplifiedEdgesOfShortestPath.add(edge_i);
                    } else {
                        // Add modified new edge
                        final Edge tempEdge = new Edge(node_i_0, node_x_1);
                        simplifiedEdgesOfShortestPath.add(tempEdge);
                        if (log) {
                            Log.i("FOOTPATH", "Created edge with length of " + tempEdge.getLength() + " and direction " + tempEdge.getDirection());
                        }
                        // Reset last node to null to distinguish if something has to be merged
                        node_x_1 = null;
                    }
                    break;
                }
            }
        }

        if (last_i != -1) {
            for (int i = last_i; i < tempEdges.size(); i++) {
                if (log) {
                    Log.i("FOOTPATH", "Adding missing edges");
                }
                simplifiedEdgesOfShortestPath.add(tempEdges.get(i));
            }
        }
        return simplifiedEdgesOfShortestPath;
    }

    private boolean equals(final Edge edge_i, final Edge edge_j, final Angle diff) {
        return isInRange(edge_i.getDirection(), edge_j.getDirection(), diff);
    }

    /**
     * Check if the difference of the given angles in degrees is less than the given allowed difference
     *
     * @param v    the first angle
     * @param t    the second angle
     * @param diff the allowed difference
     * @return true if v <= diff away from t
     */
    private static boolean isInRange(final Angle v, final Angle t, final Angle diff) {
        if (Math.abs(v.toDegrees() - t.toDegrees()) <= diff.toDegrees()) {
            return true;
        }
        return Math.abs((v.toDegrees() + diff.toDegrees()) % 360 - (t.toDegrees() + diff.toDegrees()) % 360) <= diff.toDegrees();
    }
}
