package org.labyrinth.osmand;

import net.osmand.plus.OsmandApplication;

public class FootPathFactory {

    public static FootPath createFootPath(final OsmandApplication app) {
        return new FootPath(
                FootPathDriverFactory.createFootPathDriver(app),
                () -> app.getRoutingHelper().getRoute());
    }
}
