package net.osmand.router.postman;

import net.osmand.router.RouteCalculationProgress;

class PostmanTourPlannerProgress {

    private final RouteCalculationProgress routeCalculationProgress;

    public PostmanTourPlannerProgress(final RouteCalculationProgress routeCalculationProgress) {
        this.routeCalculationProgress = routeCalculationProgress;
    }

    public void searchRouteStarted() {
        this.routeCalculationProgress.totalIterations = 2;
        this.routeCalculationProgress.iteration = 0;
    }

    public void getGraphAndStartNodeFinished() {
        this.routeCalculationProgress.nextIteration();
    }

    public void searchRouteFinished() {
        this.routeCalculationProgress.nextIteration();
    }
}
