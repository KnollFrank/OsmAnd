package org.labyrinth.osmand;

import net.osmand.plus.OsmandApplication;

public class FootPathRouteInformationListenerFactory {

    public static FootPathRouteInformationListener createFootPathRouteInformationListener(final OsmandApplication app) {
        return new FootPathRouteInformationListener(
                FootPathFactory.createFootPath(app),
                () -> app.getRoutingHelper().getRoute());
    }
}
