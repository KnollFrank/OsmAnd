package org.labyrinth.osmand;

import com.google.common.base.Supplier;

import net.osmand.data.ValueHolder;
import net.osmand.plus.routing.IRouteInformationListener;
import net.osmand.plus.routing.RouteCalculationResult;

public class FootPath implements IRouteInformationListener {

    private final FootPathDriver footPathDriver;
    private final Supplier<RouteCalculationResult> getRoute;

    public FootPath(final FootPathDriver footPathDriver, final Supplier<RouteCalculationResult> getRoute) {
        this.footPathDriver = footPathDriver;
        this.getRoute = getRoute;
    }

    // FK-TODO: RouteCalculationResult als Parameter von newRouteIsCalculated() dazufügen?
    @Override
    public void newRouteIsCalculated(final boolean newRoute, final ValueHolder<Boolean> showToast) {
        restart();
    }

    @Override
    public void routeWasCancelled() {
        stop();
    }

    @Override
    public void routeWasFinished() {
        stop();
    }

    public boolean isRunning() {
        return this.footPathDriver.isNavigating();
    }

    public void restart() {
        this.footPathDriver.restartNavigating(this.getRoute.get());
    }

    public void stop() {
        this.footPathDriver.stopNavigating();
    }
}
