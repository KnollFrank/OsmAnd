package org.labyrinth.osmand;

import android.content.Context;
import android.hardware.SensorManager;

import net.osmand.Location;
import net.osmand.plus.OsmandApplication;

import org.labyrinth.footpath.core.IStepListener;
import org.labyrinth.footpath.core.Navigator;
import org.labyrinth.footpath.core.StepDetection;
import org.labyrinth.footpath.graph.Path;

import java.util.function.Consumer;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

class FootPathDriver {

    private final StepDetection stepDetection;

    public FootPathDriver(final OsmandApplication app,
                          final Consumer<Location> setLocation,
                          final Path path,
                          final Quantity<Length> stepLength) {
        this.stepDetection =
                new StepDetection(
                        app::runInUIThread,
                        (SensorManager) app.getSystemService(Context.SENSOR_SERVICE),
                        createStepListener(setLocation, path, stepLength));
    }

    public void start() {
        this.stepDetection.load();
    }

    public void stop() {
        this.stepDetection.unload();
    }

    public boolean isStarted() {
        return this.stepDetection.isLoaded();
    }

    private static IStepListener createStepListener(final Consumer<Location> setLocation,
                                                    final Path path,
                                                    final Quantity<Length> stepLength) {
        final Navigator navigator = new Navigator(path, stepLength);
        return stepDirection -> {
            navigator.stepInDirection(stepDirection);
            setLocation.accept(Converters.asLocation(navigator.getCurrentPathPosition()));
        };
    }
}
