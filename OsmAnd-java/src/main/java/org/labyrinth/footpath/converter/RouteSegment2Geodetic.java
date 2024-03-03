package org.labyrinth.footpath.converter;

import net.osmand.binary.RouteDataObject;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;
import net.osmand.util.MapUtils;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.coordinate.Geodetic;

public class RouteSegment2Geodetic {

    public static Geodetic getGeodetic(final RouteSegment routeSegment, final short index) {
        return getGeodetic(routeSegment.getRoad(), index);
    }

    public static Geodetic getStart(final RouteSegment routeSegment) {
        return getGeodetic(routeSegment, routeSegment.getSegmentStart());
    }

    public static Geodetic getEnd(final RouteSegment routeSegment) {
        return getGeodetic(routeSegment, routeSegment.getSegmentEnd());
    }

    private static Geodetic getGeodetic(final RouteDataObject road, final short index) {
        return new Geodetic(getLatitude(road, index), getLongitude(road, index));
    }

    private static Angle getLatitude(final RouteDataObject road, final short index) {
        return new Angle(
                MapUtils.get31LatitudeY(road.getPoint31YTile(index)),
                Angle.Unit.DEGREES);
    }

    private static Angle getLongitude(final RouteDataObject road, final short index) {
        return new Angle(
                MapUtils.get31LongitudeX(road.getPoint31XTile(index)),
                Angle.Unit.DEGREES);
    }
}
