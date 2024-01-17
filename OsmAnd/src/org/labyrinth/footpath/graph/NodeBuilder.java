package org.labyrinth.footpath.graph;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;

public class NodeBuilder {

    private long id;
    private Angle lon;
    private Angle lat;
    private String name;

    public NodeBuilder withId(final long id) {
        this.id = id;
        return this;
    }

    public NodeBuilder withLon(final Angle lon) {
        this.lon = lon;
        return this;
    }

    public NodeBuilder withLat(final Angle lat) {
        this.lat = lat;
        return this;
    }

    public NodeBuilder withPosition(final Geodetic position) {
        return withLat(position.latitude).withLon(position.longitude);
    }

    public NodeBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public Node createNode() {
        return new Node(id, Geodetic.fromLatitudeLongitude(lat, lon), name);
    }
}