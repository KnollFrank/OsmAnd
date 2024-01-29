package org.labyrinth.osmand;

import android.content.Context;
import android.hardware.SensorManager;

import net.osmand.plus.OsmandApplication;

import org.labyrinth.footpath.core.StepDetection;

class FootPathDriverFactory {

    public static FootPathDriver createFootPathDriver(final OsmandApplication app) {
        return new FootPathDriver(
                location -> app.getLocationProvider().setLocationFromSimulation(location),
                stepListener ->
                        new StepDetection(
                                app::runInUIThread,
                                (SensorManager) app.getSystemService(Context.SENSOR_SERVICE),
                                stepListener),
                () -> app.getSettings().pedestrianHeight);
    }
}
