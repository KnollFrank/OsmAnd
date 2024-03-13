package org.labyrinth.coordinate;

import net.osmand.Location;
import net.osmand.data.LatLon;
import net.osmand.router.BinaryRoutePlanner.RouteSegmentPoint;

public class GeodeticFactory {

    public static Geodetic createGeodetic(final Location location) {
        return new Geodetic(
                new Angle(location.getLatitude(), Angle.Unit.DEGREES),
                new Angle(location.getLongitude(), Angle.Unit.DEGREES));
    }

    public static Geodetic createGeodetic(final LatLon latLon) {
        return new Geodetic(
                new Angle(latLon.getLatitude(), Angle.Unit.DEGREES),
                new Angle(latLon.getLongitude(), Angle.Unit.DEGREES));
    }

    public static Geodetic createGeodetic(final RouteSegmentPoint routeSegmentPoint) {
        return createGeodetic(routeSegmentPoint.getPreciseLatLon());
    }
}
