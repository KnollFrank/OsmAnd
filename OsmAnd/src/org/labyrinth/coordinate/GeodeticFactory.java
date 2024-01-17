package org.labyrinth.coordinate;

import android.location.Location;

public class GeodeticFactory {

    public static Geodetic createGeodetic(final Location location) {
        return new Geodetic(
                new Angle(location.getLatitude(), Unit.DEGREES),
                new Angle(location.getLongitude(), Unit.DEGREES));
    }

    public static Geodetic createGeodetic(final net.osmand.Location location) {
        return new Geodetic(
                new Angle(location.getLatitude(), Unit.RADIANS),
                new Angle(location.getLongitude(), Unit.RADIANS));
    }
}
