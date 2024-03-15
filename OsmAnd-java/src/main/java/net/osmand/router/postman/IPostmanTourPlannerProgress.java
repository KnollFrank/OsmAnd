package net.osmand.router.postman;

public interface IPostmanTourPlannerProgress {

    void searchRouteStarted();

    void getGraphAndStartNodeFinished();

    void getRoadPositionEquivalenceRelationStarted();

    void getRoadPositionEquivalenceRelationFinished();

    void connectedRouteSegmentsProcessorStarted();

    void connectedRouteSegmentsProcessorFinished();

    void searchRouteFinished();
}
