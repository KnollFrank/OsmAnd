package org.labyrinth.footpath.graph;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Node implements Comparable<Node> {

    private final long id;
    private final Geodetic position;
    private final String name;
    private final Set<Edge> locEdges;

    Node(final long id, final Geodetic position, final String name) {
        this.id = id;
        this.position = position;
        this.name = name;
        locEdges = new HashSet<>();
    }

    public Angle getBearing(final Node other) {
        return this.getPosition().getInitialBearingTo(other.getPosition());
    }

    public Quantity<Length> getDistanceTo(final Node other) {
        return this.getPosition().getDistanceTo(other.getPosition());
    }

    public Geodetic getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public Set<Edge> getLocEdges() {
        return locEdges;
    }

    @Override
    public int compareTo(final Node other) {
        return Long.compare(this.getId(), other.getId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node node = (Node) o;
        return id == node.id;
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
