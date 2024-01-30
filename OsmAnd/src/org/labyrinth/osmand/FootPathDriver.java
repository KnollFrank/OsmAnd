package org.labyrinth.osmand;

import net.osmand.Location;
import net.osmand.plus.routing.RouteCalculationResult;

import org.labyrinth.footpath.StepLengthProvider;
import org.labyrinth.footpath.core.IStepListener;
import org.labyrinth.footpath.core.Navigator;
import org.labyrinth.footpath.core.StepDetection;
import org.labyrinth.footpath.graph.Path;
import org.labyrinth.footpath.graph.PathFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

class FootPathDriver {

    private final StepDetection stepDetection;
    private final Supplier<Quantity<Length>> getPedestrianHeight;
    private Navigator navigator;

    public FootPathDriver(final Consumer<Location> setLocation,
                          final Function<IStepListener, StepDetection> createStepDetection,
                          final Supplier<Quantity<Length>> getPedestrianHeight) {
        this.getPedestrianHeight = getPedestrianHeight;
        this.stepDetection =
                createStepDetection.apply(
                        stepDirection -> {
                            navigator.stepInDirection(stepDirection);
                            setLocation.accept(Converters.asLocation(navigator.getCurrentPathPosition()));
                        });
    }

    public boolean tryRestartNavigating(final RouteCalculationResult route) {
        final Optional<Path> path = PathFactory.createPath(Converters.asNodes(route.getImmutableAllLocations()));
        if (!path.isPresent()) {
            return false;
        }
        restartNavigating(path.get());
        return true;
    }

    public void stopNavigating() {
        this.stepDetection.unload();
    }

    public boolean isNavigating() {
        return this.stepDetection.isLoaded();
    }

    private void restartNavigating(final Path path) {
        this.navigator =
                new Navigator(
                        path,
                        StepLengthProvider.getStepLength(this.getPedestrianHeight.get()));
        this.stepDetection.reload();
    }
}
