package org.labyrinth.osmand;

import net.osmand.plus.OsmandApplication;

import org.labyrinth.footpath.graph.Path;

import java.util.Optional;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

class FootPathDelegate {

    private final OsmandApplication app;
    private Optional<FootPathDriver> footPathDriver = Optional.empty();
    private Optional<Path> path;
    private Optional<Quantity<Length>> stepLength;
    private boolean enabled;

    public FootPathDelegate(final OsmandApplication app,
                            final Optional<Path> path,
                            final Optional<Quantity<Length>> stepLength,
                            final boolean enabled) {
        this.app = app;
        this.path = path;
        this.stepLength = stepLength;
        this.enabled = enabled;
    }

    public void stopIfEnabled() {
        if (this.enabled) {
            stop();
        }
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

    public void setPath(final Optional<Path> path) {
        this.path = path;
        if (this.enabled) {
            tryRestart();
        }
    }

    public void setStepLength(final Quantity<Length> stepLength) {
        this.stepLength = Optional.of(stepLength);
        if (this.enabled) {
            tryRestart();
        }
    }

    private void tryRestart() {
        this.footPathDriver.ifPresent(FootPathDriver::stop);
        this.footPathDriver = createFootPathDriver();
        this.footPathDriver.ifPresent(FootPathDriver::start);
    }

    private void stop() {
        this.footPathDriver.ifPresent(FootPathDriver::stop);
    }

    private Optional<FootPathDriver> createFootPathDriver() {
        if (!this.path.isPresent() || !this.stepLength.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(
                new FootPathDriver(
                        this.app,
                        location -> this.app.getLocationProvider().setLocationFromSimulation(location),
                        this.path.get(),
                        this.stepLength.get()));
    }
}
