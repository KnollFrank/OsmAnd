package org.labyrinth.footpath.graph;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Node {

    public final RoadPosition id;
    public final Geodetic position;
    public final Set<Edge> locEdges;

    public Node(final RoadPosition id, final Geodetic position) {
        this.id = id;
        this.position = position;
        locEdges = new HashSet<>();
    }

    public Angle getBearing(final Node other) {
        return position.getInitialBearingTo(other.position);
    }

    public Quantity<Length> getDistanceTo(final Node other) {
        return position.getDistanceTo(other.position);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", position=" + position +
                '}';
    }
}
