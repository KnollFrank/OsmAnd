package org.labyrinth.osmand;

import com.google.common.base.Supplier;

import net.osmand.data.ValueHolder;
import net.osmand.plus.routing.IRouteInformationListener;
import net.osmand.plus.routing.RouteCalculationResult;

class FootPathRouteInformationListener implements IRouteInformationListener {

    private final FootPath footPath;
    private final Supplier<RouteCalculationResult> getRoute;


    public FootPathRouteInformationListener(final FootPath footPath, final Supplier<RouteCalculationResult> getRoute) {
        this.footPath = footPath;
        this.getRoute = getRoute;
    }

    // FK-TODO: RouteCalculationResult als Parameter von newRouteIsCalculated() dazufügen.
    @Override
    public void newRouteIsCalculated(final boolean newRoute, final ValueHolder<Boolean> showToast) {
        this.footPath.startNavigation(this.getRoute.get());
    }

    @Override
    public void routeWasCancelled() {
        this.footPath.stopNavigation();
    }

    @Override
    public void routeWasFinished() {
        this.footPath.stopNavigation();
    }
}
