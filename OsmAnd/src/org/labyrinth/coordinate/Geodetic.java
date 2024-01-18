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

    private final LocationWrapper location;

    public Geodetic(final Angle latitude, final Angle longitude) {
        this.location = new LocationWrapper("", latitude, longitude);
    }

    public Angle getLatitude() {
        return this.location._getLatitude();
    }

    public Angle getLongitude() {
        return this.location._getLongitude();
    }

    public android.location.Location asAndroidLocation() {
        final android.location.Location location = new android.location.Location("");
        location.setLatitude(getLatitude().to(Angle.Unit.DEGREES));
        location.setLongitude(getLongitude().to(Angle.Unit.DEGREES));
        return location;
    }

    public LocationWrapper asOsmAndLocation() {
        return new LocationWrapper(this.location);
    }

    public boolean equalsDeltaDegrees(final Geodetic other, final double deltaDegrees) {
        return equalsDeltaDegrees(getLatitude(), other.getLatitude(), deltaDegrees) && equalsDeltaDegrees(getLongitude(), other.getLongitude(), deltaDegrees);
    }

    private boolean equalsDeltaDegrees(final Angle angle1, final Angle angle2, final double deltaDegrees) {
        return abs(angle1.to(Angle.Unit.DEGREES) - angle2.to(Angle.Unit.DEGREES)) < deltaDegrees;
    }

    public Quantity<Length> getDistanceTo(final Geodetic other) {
        return this.location._distanceTo(other.location);
    }

    // adapted from http://www.movable-type.co.uk/scripts/latlong.html
    public Quantity<Length> getAlongTrackDistanceTo(final PathSrcDst path) {
        final javax.measure.Unit<Length> lengthUnit = METRE;
        if (this.equals(path.getSrc())) {
            return getQuantity(0.0, lengthUnit);
        }

        final double R = EARTH_RADIUS.to(lengthUnit).getValue().doubleValue();
        final double dist13 = path.getSrc().getDistanceTo(this).to(lengthUnit).getValue().doubleValue() / R;
        final double bearing13 = path.getSrc().getInitialBearingTo(this).to(Angle.Unit.RADIANS);
        final double bearing12 = path.getSrc().getInitialBearingTo(path.getDst()).to(Angle.Unit.RADIANS);
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
        final double bearing13 = path.getSrc().getInitialBearingTo(this).to(Angle.Unit.RADIANS);
        final double bearing12 = path.getSrc().getInitialBearingTo(path.getDst()).to(Angle.Unit.RADIANS);
        final double deltaxt = asin(sin(dist13) * sin(bearing13 - bearing12));
        return getQuantity(abs(deltaxt * R), lengthUnit);
    }

    public Angle getInitialBearingTo(final Geodetic other) {
        return this.location._bearingTo(other.location);
    }

    public Geodetic moveIntoDirection(final Geodetic pos, final double factor) {
        // FK-TODO: verwende LocationSimulationUtils.middleLocation()
        // First step: Do Mercator Projection with latitude.
        final Angle.Unit unit = Angle.Unit.RADIANS;
        final double lat = getLatitude().to(unit);
        final double lon = getLongitude().to(unit);
        final double posLat = pos.getLatitude().to(unit);
        final double posLon = pos.getLongitude().to(unit);
        return new Geodetic(
                new Angle(lat + (posLat - lat) * factor, unit),
                new Angle(lon + (posLon - lon) * factor, unit));
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
