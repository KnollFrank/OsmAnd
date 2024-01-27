package org.labyrinth.osmand;

import net.osmand.Location;

import org.labyrinth.coordinate.GeodeticFactory;
import org.labyrinth.coordinate.LocationWrapper;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.NodeBuilder;
import org.labyrinth.footpath.graph.PathPosition;

import java.util.List;
import java.util.stream.Collectors;

class Converters {

    public static LocationWrapper asLocation(final PathPosition pathPosition) {
        final LocationWrapper location = pathPosition.getGeodetic().asOsmAndLocation();
        location._setBearing(pathPosition.asEdgePosition().edge.getDirection());
        return location;
    }

    public static List<Node> asNodes(final List<? extends Location> locations) {
        return locations
                .stream()
                .map(Converters::asNode)
                .collect(Collectors.toList());
    }

    private static int id = 0;

    private static Node asNode(final Location location) {
        return new NodeBuilder()
                .withId(id++)
                .withPosition(GeodeticFactory.createGeodetic(location))
                .withName("")
                .createNode();
    }
}
