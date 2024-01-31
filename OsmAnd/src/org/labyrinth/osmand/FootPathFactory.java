package org.labyrinth.osmand;

import net.osmand.plus.OsmandApplication;

import org.labyrinth.footpath.graph.Path;
import org.labyrinth.footpath.graph.PathFactory;

import java.util.Optional;

public class FootPathFactory {

    public static FootPath createFootPath(final OsmandApplication app, final boolean enabled) {
        return new FootPath(
                app,
                () -> getOptionalPath(app),
                app.getSettings().pedestrianHeight,
                enabled);
    }

    private static Optional<Path> getOptionalPath(final OsmandApplication app) {
        return PathFactory.createPath(
                Converters.asNodes(
                        app.getRoutingHelper().getRoute().getImmutableAllLocations()));
    }
}
