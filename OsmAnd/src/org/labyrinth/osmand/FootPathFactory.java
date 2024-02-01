package org.labyrinth.osmand;

import static org.labyrinth.footpath.graph.PathFactory.createPath;
import static org.labyrinth.osmand.Converters.asNodes;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.RouteCalculationResult;

import org.labyrinth.footpath.graph.Path;

import java.util.Optional;

public class FootPathFactory {

    public static FootPath createFootPath(final OsmandApplication app, final boolean enabled) {
        return new FootPath(
                app,
                () -> asOptionalPath(app.getRoutingHelper().getRoute()),
                app.getSettings().pedestrianHeight,
                enabled);
    }

    private static Optional<Path> asOptionalPath(final RouteCalculationResult route) {
        return createPath(asNodes(route.getImmutableAllLocations()));
    }
}
