package org.labyrinth.coordinate;

import net.osmand.data.LatLon;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Geodetic implements Serializable {

    private final LocationExtension location;

    public Geodetic(final Angle latitude, final Angle longitude) {
        this.location = new LocationExtension("", latitude, longitude);
    }

    public Angle getLatitude() {
        return this.location._getLatitude();
    }

    public Angle getLongitude() {
        return this.location._getLongitude();
    }

    public LocationExtension asOsmAndLocation() {
        return new LocationExtension(this.location);
    }

    public LatLon asLatLon() {
        return new LatLon(
                getLatitude().to(Angle.Unit.DEGREES),
                getLongitude().to(Angle.Unit.DEGREES));
    }

    public Quantity<Length> getDistanceTo(final Geodetic other) {
        return this.location._distanceTo(other.location);
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
