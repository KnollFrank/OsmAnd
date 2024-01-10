package org.labyrinth.footpath.graph;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;

import static org.labyrinth.common.MeasureUtils.sum;

public class Edges {

    public static Quantity<Length> getLength(final List<Edge> edges) {
        return sum(edges.stream().map(Edge::getLength));
    }
}
