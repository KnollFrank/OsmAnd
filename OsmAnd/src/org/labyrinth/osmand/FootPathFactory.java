package org.labyrinth.osmand;

import android.content.Context;
import android.hardware.SensorManager;

import net.osmand.plus.OsmandApplication;

import org.labyrinth.footpath.core.StepDetection;

class FootPathFactory {

    public static FootPath createFootPath(final OsmandApplication app) {
        return new FootPath(
                app.getLocationProvider()::setLocationFromSimulation,
                stepListener ->
                        new StepDetection(
                                app::runInUIThread,
                                (SensorManager) app.getSystemService(Context.SENSOR_SERVICE),
                                stepListener));
    }
}
