package net.osmand.router.postman;

import net.osmand.router.RouteCalculationProgress;

class PostmanTourPlannerProgress implements IPostmanTourPlannerProgress {

    private final RouteCalculationProgress routeCalculationProgress;

    public PostmanTourPlannerProgress(final RouteCalculationProgress routeCalculationProgress) {
        this.routeCalculationProgress = routeCalculationProgress;
        this.routeCalculationProgress.totalIterations = 7;
        this.routeCalculationProgress.iteration = 0;
    }

    @Override
    public void searchRouteStarted() {
        this.routeCalculationProgress.nextIteration();
    }

    @Override
    public void getGraphAndStartNodeFinished() {
        this.routeCalculationProgress.nextIteration();
    }

    @Override
    public void getRoadPositionEquivalenceRelationStarted() {
        this.routeCalculationProgress.nextIteration();
    }

    @Override
    public void getRoadPositionEquivalenceRelationFinished() {
        this.routeCalculationProgress.nextIteration();
    }

    @Override
    public void connectedRouteSegmentsProcessorStarted() {
        this.routeCalculationProgress.nextIteration();
    }

    @Override
    public void connectedRouteSegmentsProcessorFinished() {
        this.routeCalculationProgress.nextIteration();
    }

    @Override
    public void searchRouteFinished() {
        this.routeCalculationProgress.nextIteration();
    }
}
