package org.labyrinth.coordinate;

import org.labyrinth.common.MeasureUtils;
import org.labyrinth.footpath.graph.Edge;
import org.labyrinth.footpath.graph.EdgePosition;
import org.labyrinth.model.PathSrcDst;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Set;

import static org.labyrinth.common.MeasureUtils.divide;
import static org.labyrinth.common.Utils.clampValueToMinMax;

public class DistanceAlgorithms {

    public static Edge getEdgeWithMinDistanceToPos(final Set<Edge> edges, final Geodetic pos) {
        return edges
                .stream()
                .min((edge1, edge2) -> MeasureUtils.COMPARATOR.compare(getDistance(edge1, pos), getDistance(edge2, pos)))
                .get();
    }

    public static EdgePosition getPositionOfProjectedGeodeticOnEdge(final Edge edge, final Geodetic pos) {
        return new EdgePosition(
                edge,
                clampValueToMinMax(getFractionOfEdgeLength(edge, pos), 0.0, 1.0));
    }

    private static double getFractionOfEdgeLength(final Edge edge, final Geodetic pos) {
        return divide(getAlongTrackDistanceTo(edge, pos), edge.getLength());
    }

    static Quantity<Length> getDistance(final Edge edge, final Geodetic pos) {
        final double fractionOfEdgeLength = getFractionOfEdgeLength(edge, pos);
        if (fractionOfEdgeLength <= 0.0) {
            return pos.getDistanceTo(edge.getSource().getPosition());
        }
        if (fractionOfEdgeLength >= 1.0) {
            return pos.getDistanceTo(edge.getTarget().getPosition());
        }
        return pos.crossTrackDistanceTo(
                new PathSrcDst(
                        edge.getSource().getPosition(),
                        edge.getTarget().getPosition()));
    }

    private static Quantity<Length> getAlongTrackDistanceTo(final Edge edge, final Geodetic pos) {
        return pos.getAlongTrackDistanceTo(
                new PathSrcDst(
                        edge.getSource().getPosition(),
                        edge.getTarget().getPosition()));
    }
}
