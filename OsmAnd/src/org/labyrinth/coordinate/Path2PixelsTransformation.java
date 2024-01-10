package org.labyrinth.coordinate;

import com.google.common.collect.ImmutableList;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.Path;

import java.util.List;
import java.util.stream.Collectors;

public class Path2PixelsTransformation {

    public static List<Geodetic> getGeodetics(final Path path) {
        return ImmutableList
                .<Geodetic>builder()
                .add(path.src.getGeodetic())
                .addAll(getPositions(path.intermediateNodes))
                .add(path.dst.getGeodetic())
                .build();
    }

    private static List<Geodetic> getPositions(final List<Node> nodes) {
        return nodes
                .stream()
                .map(Node::getPosition)
                .collect(Collectors.toList());
    }
}
