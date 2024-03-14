package org.labyrinth.coordinate;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.Units.METRE;

import net.osmand.Location;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class LocationExtension extends Location {

    private static final Angle.Unit UNIT4ANGLES = Angle.Unit.DEGREES;

    public LocationExtension(final String provider, final Angle lat, final Angle lon) {
        super(provider, lat.to(UNIT4ANGLES), lon.to(UNIT4ANGLES));
    }

    public LocationExtension(final Location l) {
        super(l);
    }

    public Angle _getLatitude() {
        return new Angle(getLatitude(), UNIT4ANGLES);
    }

    public Angle _getLongitude() {
        return new Angle(getLongitude(), UNIT4ANGLES);
    }

    public Quantity<Length> _distanceTo(final Location dest) {
        // FK-TODO: oder "return getQuantity(net.osmand.util.MapUtils.getDistance(this, dest), METRE);"?
        return getQuantity(this.distanceTo(dest), METRE);
    }

    public void _setBearing(final Angle bearing) {
        setBearing((float) bearing.to(UNIT4ANGLES));
    }

    public Angle _getBearing() {
        return new Angle(getBearing(), UNIT4ANGLES);
    }

    public Angle _bearingTo(Location dest) {
        return new Angle(this.bearingTo(dest), UNIT4ANGLES);
    }
}
