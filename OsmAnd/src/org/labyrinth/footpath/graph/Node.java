package org.labyrinth.footpath.graph;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Node {

    public final long id;
    public final Geodetic position;
    public final String name;

    Node(final long id, final Geodetic position, final String name) {
        this.id = id;
        this.position = position;
        this.name = name;
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
