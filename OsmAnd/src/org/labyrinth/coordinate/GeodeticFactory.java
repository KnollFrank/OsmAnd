package org.labyrinth.coordinate;

import net.osmand.Location;

public class GeodeticFactory {

    public static Geodetic createGeodetic(final Location location) {
        return new Geodetic(
                new Angle(location.getLatitude(), Angle.Unit.DEGREES),
                new Angle(location.getLongitude(), Angle.Unit.DEGREES));
    }
}
