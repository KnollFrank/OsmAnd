package org.labyrinth.footpath.converter;

import net.osmand.router.postman.IPostmanTourPlannerProgress;

public class PostmanTourPlannerProgressTestFactory {

    public static IPostmanTourPlannerProgress createDummyPostmanTourPlannerProgress() {
        return new IPostmanTourPlannerProgress() {

            @Override
            public void searchRouteStarted() {

            }

            @Override
            public void getGraphAndStartNodeFinished() {

            }

            @Override
            public void getRoadPositionEquivalenceRelationStarted() {

            }

            @Override
            public void getRoadPositionEquivalenceRelationFinished() {

            }

            @Override
            public void connectedRouteSegmentsProcessorStarted() {

            }

            @Override
            public void connectedRouteSegmentsProcessorFinished() {

            }

            @Override
            public void searchRouteFinished() {

            }
        };
    }
}
