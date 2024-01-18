package net.osmand.plus.simulation;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.Units.METRE;

import android.content.Context;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;

import org.apache.commons.logging.Log;
import org.labyrinth.coordinate.GeodeticFactory;
import org.labyrinth.coordinate.LocationWrapper;
import org.labyrinth.footpath.StepLengthProvider;
import org.labyrinth.footpath.core.Navigator;
import org.labyrinth.footpath.core.StepDetection;
import org.labyrinth.footpath.graph.Node;
import org.labyrinth.footpath.graph.NodeBuilder;
import org.labyrinth.footpath.graph.PathFactory;
import org.labyrinth.footpath.graph.PathPosition;

import java.util.List;
import java.util.stream.Collectors;

public class OsmAndLocationSimulation2 extends OsmAndLocationSimulation {

    private static final Log LOG = PlatformUtil.getLog(OsmAndLocationSimulation2.class);

    private final StepDetection stepDetection;
    private Navigator navigator;

    public OsmAndLocationSimulation2(@NonNull OsmandApplication app) {
        super(app);
        this.stepDetection =
                new StepDetection(
                        app::runInUIThread,
                        (SensorManager) app.getSystemService(Context.SENSOR_SERVICE),
                        stepDirection -> {
                            LOG.info("step detected: " + stepDirection);
                            this.navigator.stepInDirection(stepDirection);
                            app.getLocationProvider().setLocationFromSimulation(
                                    asLocation(this.navigator.getCurrentPathPosition()));
                        });
    }

    private static Location asLocation(final PathPosition pathPosition) {
        final LocationWrapper location = pathPosition.getGeodetic().asOsmAndLocation();
        location._setBearing(pathPosition.asEdgePosition().edge.getDirection());
        return location;
    }

    @Override
    public boolean isRouteAnimating() {
        return this.stepDetection.isLoaded();
    }

    @Override
    public void startSimulationThread(@NonNull final OsmandApplication app, @NonNull final List<SimulatedLocation> directions, final boolean useLocationTime, final float coeff) {
        this.navigator =
                new Navigator(
                        PathFactory.createPath(asNodes(directions)),
                        StepLengthProvider.getStepLength(getQuantity(187.0, CENTI(METRE))));
        notifyListeners(true);
        this.stepDetection.load();
    }

    private static List<Node> asNodes(final List<? extends Location> locations) {
        return locations
                .stream()
                .map(OsmAndLocationSimulation2::asNode)
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

    @Override
    public void stop() {
        super.stop();
        this.stepDetection.unload();
    }
}
