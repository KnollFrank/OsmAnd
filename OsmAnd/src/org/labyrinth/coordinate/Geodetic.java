package org.labyrinth.coordinate;

import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.Units.METRE;

import org.labyrinth.model.PathSrcDst;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Geodetic implements Serializable {

    public static final Quantity<Length> EARTH_RADIUS = getQuantity(6378137.0, METRE);

    private final net.osmand.Location location;

    public Geodetic(final Angle latitude, final Angle longitude) {
        this.location =
                new net.osmand.Location(
                        "",
                        latitude.toDegrees(),
                        longitude.toDegrees());
    }

    public Angle getLatitude() {
        return new Angle(this.location.getLatitude(), Angle.Unit.DEGREES);
    }

    public Angle getLongitude() {
        return new Angle(this.location.getLongitude(), Angle.Unit.DEGREES);
    }

    public android.location.Location asAndroidLocation() {
        final android.location.Location location = new android.location.Location("");
        location.setLatitude(getLatitude().toDegrees());
        location.setLongitude(getLongitude().toDegrees());
        return location;
    }

    public net.osmand.Location asOsmAndLocation() {
        return new net.osmand.Location(this.location);
    }

    public boolean equalsDeltaDegrees(final Geodetic other, final double deltaDegrees) {
        return equalsDeltaDegrees(getLatitude(), other.getLatitude(), deltaDegrees) && equalsDeltaDegrees(getLongitude(), other.getLongitude(), deltaDegrees);
    }

    private boolean equalsDeltaDegrees(final Angle angle1, final Angle angle2, final double deltaDegrees) {
        return abs(angle1.toDegrees() - angle2.toDegrees()) < deltaDegrees;
    }

    public Quantity<Length> getDistanceTo(final Geodetic other) {
        // FK-TODO: oder MapUtils.getDistance() verwenden?
        // FK-TODO: stimmt die Einheit METRE als Rückgabeeinheit von location.distanceTo()?
        return getQuantity(this.location.distanceTo(other.location), METRE);
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
        return new Angle(
                this.location.bearingTo(other.location),
                Angle.Unit.DEGREES);
    }

    public Geodetic moveIntoDirection(final Geodetic pos, final double factor) {
        // FK-TODO: verwende LocationSimulationUtils.middleLocation()
        // First step: Do Mercator Projection with latitude.
        final double lat = getLatitude().toRadians();
        final double lon = getLongitude().toRadians();
        final double posLat = pos.getLatitude().toRadians();
        final double posLon = pos.getLongitude().toRadians();
        return new Geodetic(
                new Angle(lat + (posLat - lat) * factor, Angle.Unit.RADIANS),
                new Angle(lon + (posLon - lon) * factor, Angle.Unit.RADIANS));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Geodetic geodetic = (Geodetic) o;
        return getLatitude().equals(geodetic.getLatitude()) &&
                getLongitude().equals(geodetic.getLongitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Geodetic.class.getSimpleName() + "[", "]")
                .add("latitude=" + getLatitude())
                .add("longitude=" + getLongitude())
                .toString();
    }
}
