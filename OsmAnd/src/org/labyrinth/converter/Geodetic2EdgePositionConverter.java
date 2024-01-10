package org.labyrinth.converter;

import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EdgePosition;

import java.util.Set;

import static org.labyrinth.coordinate.DistanceAlgorithms.getEdgeWithMinDistanceToPos;
import static org.labyrinth.coordinate.DistanceAlgorithms.getPositionOfProjectedGeodeticOnEdge;

public class Geodetic2EdgePositionConverter {

    private final Set<Edge> edges;

    public Geodetic2EdgePositionConverter(final Set<Edge> edges) {
        this.edges = edges;
    }

    public EdgePosition geodetic2EdgePosition(final Geodetic geodetic) {
        return getPositionOfProjectedGeodeticOnEdge(
                getEdgeWithMinDistanceToPos(edges, geodetic),
                geodetic);
    }
}
