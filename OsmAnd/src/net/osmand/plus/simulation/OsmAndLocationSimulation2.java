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
import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;
import org.labyrinth.coordinate.Unit;
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

    private static Location asLocation(final PathPosition currentPathPosition) {
        final Location location = asLocation(currentPathPosition.getGeodetic());
        location.setBearing((float) currentPathPosition.asEdgePosition().edge.getDirection().toDegrees());
        return location;
    }

    private static Location asLocation(final Geodetic geodetic) {
        return new Location(
                "",
                geodetic.latitude.toRadians(),
                geodetic.longitude.toRadians());
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
                        // StepLengthProvider.getStepLength(getQuantity(272.0, CENTI(METRE)))
                        getQuantity(15.0, METRE));
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
                .withPosition(asGeodetic(location))
                .withName("")
                .createNode();
    }

    private static Geodetic asGeodetic(final Location location) {
        return Geodetic.fromLatitudeLongitude(
                new Angle(location.getLatitude(), Unit.RADIANS),
                new Angle(location.getLongitude(), Unit.RADIANS));
    }

    @Override
    public void stop() {
        super.stop();
        this.stepDetection.unload();
    }
}
