package org.labyrinth.osmand;

import net.osmand.data.ValueHolder;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.IRouteInformationListener;

import org.labyrinth.footpath.graph.Path;

import java.util.Optional;
import java.util.function.Supplier;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class FootPath implements IRouteInformationListener {

    private final OsmandApplication app;
    private Optional<FootPathDriver> footPathDriver = Optional.empty();
    private final Supplier<Optional<Path>> optionalPathSupplier;
    private Quantity<Length> pedestrianHeight;
    private boolean enabled;

    public FootPath(final OsmandApplication app,
                    final Supplier<Optional<Path>> optionalPathSupplier,
                    final Quantity<Length> pedestrianHeight,
                    final boolean enabled) {
        this.app = app;
        this.optionalPathSupplier = optionalPathSupplier;
        this.pedestrianHeight = pedestrianHeight;
        this.enabled = enabled;
    }

    @Override
    public void newRouteIsCalculated(final boolean newRoute, final ValueHolder<Boolean> showToast) {
        if (!this.enabled) return;
        tryRestart();
    }

    @Override
    public void routeWasCancelled() {
        if (!this.enabled) return;
        stop();
    }

    @Override
    public void routeWasFinished() {
        if (!this.enabled) return;
        stop();
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            tryRestart();
        } else {
            stop();
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setPedestrianHeight(final Quantity<Length> pedestrianHeight) {
        this.pedestrianHeight = pedestrianHeight;
        if (this.enabled) {
            tryRestart();
        }
    }

    private void tryRestart() {
        stop();
        this.footPathDriver = createFootPathDriver();
        this.footPathDriver.ifPresent(FootPathDriver::start);
    }

    private void stop() {
        this.footPathDriver.ifPresent(FootPathDriver::stop);
    }

    private Optional<FootPathDriver> createFootPathDriver() {
        return this
                .getOptionalPath()
                .map(path ->
                        new FootPathDriver(
                                this.app,
                                location -> this.app.getLocationProvider().setLocationFromSimulation(location),
                                path,
                                this.pedestrianHeight));
    }

    private Optional<Path> getOptionalPath() {
        return this.optionalPathSupplier.get();
    }
}
