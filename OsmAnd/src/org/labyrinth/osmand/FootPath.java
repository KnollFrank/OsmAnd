package org.labyrinth.osmand;

import com.google.common.base.Supplier;

import net.osmand.data.ValueHolder;
import net.osmand.plus.routing.IRouteInformationListener;
import net.osmand.plus.routing.RouteCalculationResult;

public class FootPath implements IRouteInformationListener {

    private final FootPathDriver footPathDriver;
    private final Supplier<RouteCalculationResult> getRoute;
    private boolean enabled;

    public FootPath(
            final FootPathDriver footPathDriver,
            final Supplier<RouteCalculationResult> getRoute,
            final boolean enabled) {
        this.footPathDriver = footPathDriver;
        this.getRoute = getRoute;
        this.enabled = enabled;
    }

    @Override
    public void newRouteIsCalculated(final boolean newRoute, final ValueHolder<Boolean> showToast) {
        if (!isEnabled()) return;
        restart();
    }

    @Override
    public void routeWasCancelled() {
        if (!isEnabled()) return;
        stop();
    }

    @Override
    public void routeWasFinished() {
        if (!isEnabled()) return;
        stop();
    }

    public boolean isRunning() {
        return this.footPathDriver.isNavigating();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            restart();
        } else {
            stop();
        }
    }

    private void restart() {
        this.footPathDriver.restartNavigating(this.getRoute.get());
    }

    private void stop() {
        this.footPathDriver.stopNavigating();
    }
}
