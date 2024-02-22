package org.labyrinth.footpath.graph;

import static org.labyrinth.common.MeasureUtils.sum;

import java.util.List;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Edges {

    public static Quantity<Length> getLength(final List<Edge> edges) {
        return sum(edges.stream().map(Edge::getLength));
    }
}
