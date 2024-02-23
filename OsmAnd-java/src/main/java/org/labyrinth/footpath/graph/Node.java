package org.labyrinth.footpath.graph;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Node implements Comparable<Node> {

    public final RoadPosition id;
    public final Geodetic position;
    // FK-TODO: remove name field
    public final String name;
    public final Set<Edge> locEdges;

    public Node(final RoadPosition id, final Geodetic position, final String name) {
        this.id = id;
        this.position = position;
        this.name = name;
        locEdges = new HashSet<>();
    }

    public Angle getBearing(final Node other) {
        return position.getInitialBearingTo(other.position);
    }

    public Quantity<Length> getDistanceTo(final Node other) {
        return position.getDistanceTo(other.position);
    }

    @Override
    public int compareTo(final Node other) {
        return this.id.compareTo(other.id);
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
                ", name='" + name + '\'' +
                '}';
    }
}
