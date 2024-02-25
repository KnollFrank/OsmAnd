package org.labyrinth.osmand;

import net.osmand.Location;

import org.labyrinth.coordinate.GeodeticFactory;
import org.labyrinth.coordinate.LocationExtension;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.PathPosition;
import org.labyrinth.footpath.graph.RoadPosition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Converters {

    public static LocationExtension asLocation(final PathPosition pathPosition) {
        final LocationExtension location = pathPosition.getGeodetic().asOsmAndLocation();
        location._setBearing(pathPosition.asEdgePosition().edge.getDirection());
        return location;
    }

    public static List<Node> asNodes(final List<? extends Location> locations) {
        return IntStream
                .range(0, locations.size())
                .mapToObj(i -> asNode(i, locations.get(i)))
                .collect(Collectors.toList());
    }

    private static Node asNode(final int id, final Location location) {
        return new Node(
                new RoadPosition(-1, id),
                GeodeticFactory.createGeodetic(location));
    }
}
