package org.labyrinth.coordinate;

public class GeodeticFactory {

    public static Geodetic createGeodetic(final android.location.Location location) {
        return new Geodetic(
                new Angle(location.getLatitude(), Unit.DEGREES),
                new Angle(location.getLongitude(), Unit.DEGREES));
    }

    public static Geodetic createGeodetic(final net.osmand.Location location) {
        return new Geodetic(
                new Angle(location.getLatitude(), Unit.DEGREES),
                new Angle(location.getLongitude(), Unit.DEGREES));
    }
}
