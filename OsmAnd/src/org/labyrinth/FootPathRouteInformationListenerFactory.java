package org.labyrinth;

import android.content.Context;
import android.hardware.SensorManager;

import net.osmand.plus.OsmandApplication;

import org.labyrinth.footpath.core.StepDetection;

public class FootPathRouteInformationListenerFactory {

    public static FootPathRouteInformationListener createFootPathRouteInformationListener(final OsmandApplication app) {
        return new FootPathRouteInformationListener(
                app.getLocationProvider()::setLocationFromSimulation,
                app.getRoutingHelper()::getRoute,
                stepListener ->
                        new StepDetection(
                                app::runInUIThread,
                                (SensorManager) app.getSystemService(Context.SENSOR_SERVICE),
                                stepListener));
    }
}
