package org.labyrinth.osmand;

import static org.labyrinth.footpath.graph.PathFactory.createPath;
import static org.labyrinth.osmand.Converters.asNodes;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.RouteCalculationResult;

import org.labyrinth.footpath.StepLengthProvider;
import org.labyrinth.footpath.graph.Path;

import java.util.Optional;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class FootPathFactory {

    public static FootPath createFootPath(final OsmandApplication app, final boolean enabled) {
        return new FootPath(
                app,
                () -> asPath(app.getRoutingHelper().getRoute()),
                getStepLength(app),
                enabled);
    }

    private static Optional<Path> asPath(final RouteCalculationResult route) {
        return createPath(asNodes(route.getImmutableAllLocations()));
    }

    private static Optional<Quantity<Length>> getStepLength(final OsmandApplication app) {
        return app
                .getSettings()
                .getPedestrianHeight()
                .map(StepLengthProvider::getStepLength);
    }
}
