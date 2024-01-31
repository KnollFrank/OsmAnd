package org.labyrinth.osmand;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.RouteCalculationResult;

import org.labyrinth.footpath.graph.Path;
import org.labyrinth.footpath.graph.PathFactory;

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
        return PathFactory.createPath(
                Converters.asNodes(
                        route.getImmutableAllLocations()));
    }
}
