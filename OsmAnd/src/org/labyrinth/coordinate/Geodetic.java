package org.labyrinth.coordinate;

import android.location.Location;

import org.labyrinth.model.PathSrcDst;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.Units.METRE;

public class Geodetic implements Serializable {

    public static final Quantity<Length> EARTH_RADIUS = getQuantity(6378137.0, METRE);

    private final Angle latitude;
    private final Angle longitude;

    private Geodetic(final Angle latitude, final Angle longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Geodetic fromLatitudeLongitude(final Angle latitude, final Angle longitude) {
        return new Geodetic(latitude, longitude);
    }

    public static Geodetic fromLocation(final Location location) {
        return fromLatitudeLongitude(
                new Angle(location.getLatitude(), Unit.DEGREES),
                new Angle(location.getLongitude(), Unit.DEGREES));
    }

    public Angle getLatitude() {
        return latitude;
    }

    public Angle getLongitude() {
        return longitude;
    }

    public Location asLocation() {
        final Location location = new Location("");
        location.setLatitude(latitude.toDegrees());
        location.setLongitude(longitude.toDegrees());
        return location;
    }

    public boolean equalsDeltaDegrees(final Geodetic other, final double deltaDegrees) {
        return equalsDeltaDegrees(latitude, other.latitude, deltaDegrees) && equalsDeltaDegrees(longitude, other.longitude, deltaDegrees);
    }

    private boolean equalsDeltaDegrees(final Angle angle1, final Angle angle2, final double deltaDegrees) {
        return abs(angle1.toDegrees() - angle2.toDegrees()) < deltaDegrees;
    }

    // adapted from http://www.movable-type.co.uk/scripts/latlong.html
    public Quantity<Length> getDistanceTo(final Geodetic other) {
        final double lat1 = latitude.toRadians();
        final double lat2 = other.latitude.toRadians();
        final double dLon = other.longitude.sub(longitude).toRadians();
        final double dLat = lat2 - lat1;

        final double a = sin(dLat / 2) * sin(dLat / 2) + cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2);
        final double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return EARTH_RADIUS.multiply(c);
    }

    // adapted from http://www.movable-type.co.uk/scripts/latlong.html
    public Quantity<Length> getAlongTrackDistanceTo(final PathSrcDst path) {
        final javax.measure.Unit<Length> lengthUnit = METRE;
        if (this.equals(path.getSrc())) {
            return getQuantity(0.0, lengthUnit);
        }

        final double R = EARTH_RADIUS.to(lengthUnit).getValue().doubleValue();
        final double dist13 = path.getSrc().getDistanceTo(this).to(lengthUnit).getValue().doubleValue() / R;
        final double bearing13 = path.getSrc().getInitialBearingTo(this).toRadians();
        final double bearing12 = path.getSrc().getInitialBearingTo(path.getDst()).toRadians();
        final double deltaxt = asin(sin(dist13) * sin(bearing13 - bearing12));
        final double deltaat = acos(cos(dist13) / abs(cos(deltaxt)));
        return getQuantity((deltaat * signum(cos(bearing12 - bearing13))) * R, lengthUnit);
    }

    // adapted from http://www.movable-type.co.uk/scripts/latlong.html
    public Quantity<Length> crossTrackDistanceTo(final PathSrcDst path) {
        final javax.measure.Unit<Length> lengthUnit = METRE;
        if (this.equals(path.getSrc())) {
            return getQuantity(0.0, lengthUnit);
        }

        final double R = EARTH_RADIUS.to(lengthUnit).getValue().doubleValue();
        final double dist13 = path.getSrc().getDistanceTo(this).to(lengthUnit).getValue().doubleValue() / R;
        final double bearing13 = path.getSrc().getInitialBearingTo(this).toRadians();
        final double bearing12 = path.getSrc().getInitialBearingTo(path.getDst()).toRadians();
        final double deltaxt = asin(sin(dist13) * sin(bearing13 - bearing12));
        return getQuantity(abs(deltaxt * R), lengthUnit);
    }

    // adapted from http://www.movable-type.co.uk/scripts/latlong.html
    public Angle getInitialBearingTo(final Geodetic other) {
        final double lat1 = latitude.toRadians();
        final double lat2 = other.latitude.toRadians();
        final double dLon = other.longitude.sub(longitude).toRadians();
        final double x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon);
        final double y = sin(dLon) * cos(lat2);
        final double b = atan2(y, x);
        return new Angle(-b, Unit.RADIANS).wrap0To360Degrees();
    }

    public Geodetic moveIntoDirection(final Geodetic pos, final double factor) {
        // First step: Do Mercator Projection with latitude.
        final double lat = latitude.toRadians();
        final double lon = longitude.toRadians();
        final double posLat = pos.latitude.toRadians();
        final double posLon = pos.longitude.toRadians();
        return Geodetic.fromLatitudeLongitude(
                new Angle(lat + (posLat - lat) * factor, Unit.RADIANS),
                new Angle(lon + (posLon - lon) * factor, Unit.RADIANS));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Geodetic geodetic = (Geodetic) o;
        return latitude.equals(geodetic.latitude) &&
                longitude.equals(geodetic.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Geodetic.class.getSimpleName() + "[", "]")
                .add("latitude=" + latitude)
                .add("longitude=" + longitude)
                .toString();
    }
}
