package net.osmand.plus.simulation;

import android.content.Context;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;

import org.apache.commons.logging.Log;
import org.labyrinth.footpath.core.StepDetection;

import java.util.List;

public class OsmAndLocationSimulation2 extends OsmAndLocationSimulation {

    private static final Log LOG = PlatformUtil.getLog(OsmAndLocationSimulation2.class);

    private final StepDetection stepDetection;

    public OsmAndLocationSimulation2(@NonNull OsmandApplication app) {
        super(app);
        this.stepDetection =
                new StepDetection(
                        app::runInUIThread,
                        (SensorManager) app.getSystemService(Context.SENSOR_SERVICE),
                        stepDirection -> LOG.info("step detected: " + stepDirection));
    }

    @Override
    public boolean isRouteAnimating() {
        return this.stepDetection.isLoaded();
    }

    @Override
    public void startSimulationThread(@NonNull final OsmandApplication app, @NonNull final List<SimulatedLocation> directions, final boolean useLocationTime, final float coeff) {
        notifyListeners(true);
        this.stepDetection.load();
    }

    @Override
    public void stop() {
        super.stop();
        this.stepDetection.unload();
    }
}
