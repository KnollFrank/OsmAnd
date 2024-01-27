package org.labyrinth;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.Units.METRE;

import com.google.common.base.Supplier;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.data.ValueHolder;
import net.osmand.plus.routing.IRouteInformationListener;
import net.osmand.plus.routing.RouteCalculationResult;

import org.apache.commons.logging.Log;
import org.labyrinth.coordinate.GeodeticFactory;
import org.labyrinth.coordinate.LocationWrapper;
import org.labyrinth.footpath.StepLengthProvider;
import org.labyrinth.footpath.core.IStepListener;
import org.labyrinth.footpath.core.Navigator;
import org.labyrinth.footpath.core.StepDetection;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.NodeBuilder;
import org.labyrinth.footpath.graph.PathFactory;
import org.labyrinth.footpath.graph.PathPosition;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

class FootPathRouteInformationListener implements IRouteInformationListener {

    private static final Log LOG = PlatformUtil.getLog(FootPathRouteInformationListener.class);

    private final Supplier<RouteCalculationResult> getRoute;
    private final StepDetection stepDetection;
    private Navigator navigator;

    public FootPathRouteInformationListener(
            final Consumer<Location> setLocation,
            final Supplier<RouteCalculationResult> getRoute,
            final Function<IStepListener, StepDetection> createStepDetection) {
        this.getRoute = getRoute;
        this.stepDetection =
                createStepDetection.apply(
                        stepDirection -> {
                            LOG.info("step detected: " + stepDirection);
                            navigator.stepInDirection(stepDirection);
                            setLocation.accept(asLocation(navigator.getCurrentPathPosition()));
                        });
    }

    // FK-TODO: RouteCalculationResult als Parameter von newRouteIsCalculated() dazufügen.
    @Override
    public void newRouteIsCalculated(final boolean newRoute, final ValueHolder<Boolean> showToast) {
        final List<Location> locations = this.getRoute.get().getImmutableAllLocations();
        LOG.info(
                String.format(
                        "FKK: IRouteInformationListener.newRouteIsCalculated() %d, %s: ",
                        locations.size(),
                        locations));
        this.navigator =
                new Navigator(
                        PathFactory.createPath(asNodes(locations)),
                        StepLengthProvider.getStepLength(getQuantity(187.0, CENTI(METRE))));
        this.stepDetection.load();
    }

    @Override
    public void routeWasCancelled() {
        LOG.info("FKK: IRouteInformationListener.routeWasCancelled()");
        this.stepDetection.unload();
    }

    @Override
    public void routeWasFinished() {
        LOG.info("FKK: IRouteInformationListener.routeWasFinished()");
        this.stepDetection.unload();
    }

    private static LocationWrapper asLocation(final PathPosition pathPosition) {
        final LocationWrapper location = pathPosition.getGeodetic().asOsmAndLocation();
        location._setBearing(pathPosition.asEdgePosition().edge.getDirection());
        return location;
    }

    private static List<Node> asNodes(final List<? extends Location> locations) {
        return locations
                .stream()
                .map(FootPathRouteInformationListener::asNode)
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
