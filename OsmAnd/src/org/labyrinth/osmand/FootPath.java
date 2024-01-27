package org.labyrinth.osmand;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.Units.METRE;

import net.osmand.Location;
import net.osmand.plus.routing.RouteCalculationResult;

import org.labyrinth.footpath.StepLengthProvider;
import org.labyrinth.footpath.core.IStepListener;
import org.labyrinth.footpath.core.Navigator;
import org.labyrinth.footpath.core.StepDetection;
import org.labyrinth.footpath.graph.PathFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

class FootPath {

    private final StepDetection stepDetection;
    private Navigator navigator;

    public FootPath(final Consumer<Location> setLocation,
                    final Function<IStepListener, StepDetection> createStepDetection) {
        this.stepDetection =
                createStepDetection.apply(
                        stepDirection -> {
                            navigator.stepInDirection(stepDirection);
                            setLocation.accept(Converters.asLocation(navigator.getCurrentPathPosition()));
                        });
    }

    public void startNavigation(final RouteCalculationResult route) {
        final List<Location> locations = route.getImmutableAllLocations();
        this.navigator =
                new Navigator(
                        PathFactory.createPath(Converters.asNodes(locations)),
                        StepLengthProvider.getStepLength(getQuantity(187.0, CENTI(METRE))));
        this.stepDetection.load();
    }

    public void stopNavigation() {
        this.stepDetection.unload();
    }
}
