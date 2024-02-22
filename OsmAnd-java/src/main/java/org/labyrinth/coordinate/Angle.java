package org.labyrinth.coordinate;

import java.io.Serializable;
import java.util.Objects;

public class Angle implements Serializable {

    private static final double M_2PI = Math.PI * 2;

    public enum Unit {

        DEGREES, RADIANS
    }

    private final double angleInRadians0To2PI;

    public static final Angle ZERO = new Angle(0, Unit.DEGREES);

    public Angle(final double angle, final Unit unit) {
        this.angleInRadians0To2PI = normalize0To2PI(getAngleInRadians(angle, unit));
    }

    public double to(final Unit unit) {
        switch (unit) {
            case DEGREES:
                return Math.toDegrees(angleInRadians0To2PI);
            case RADIANS:
                return angleInRadians0To2PI;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Angle add(final Angle other) {
        return new Angle(this.angleInRadians0To2PI + other.angleInRadians0To2PI, Unit.RADIANS);
    }

    public Angle sub(final Angle other) {
        return new Angle(this.angleInRadians0To2PI - other.angleInRadians0To2PI, Unit.RADIANS);
    }

    public Angle get0To180DegreesDifferenceTo(final Angle other) {
        final double angle0To360Degrees = this.sub(other).to(Unit.DEGREES);
        final double angle0To180Degrees = angle0To360Degrees > 180 ? 360 - angle0To360Degrees : angle0To360Degrees;
        return new Angle(angle0To180Degrees, Unit.DEGREES);
    }

    @Override
    public int hashCode() {
        return Objects.hash(angleInRadians0To2PI);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Angle angle = (Angle) o;
        return Double.compare(angle.angleInRadians0To2PI, angleInRadians0To2PI) == 0;
    }

    @Override
    public String toString() {
        return "Angle{angle = " + to(Unit.DEGREES) + "°}";
    }

    private static double getAngleInRadians(final double angle, final Unit unit) {
        switch (unit) {
            case DEGREES:
                return Math.toRadians(angle);
            case RADIANS:
                return angle;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static double normalize0To2PI(final double angleInRadians) {
        if (0 <= angleInRadians && angleInRadians < M_2PI) {
            return angleInRadians;
        }
        return ((angleInRadians % M_2PI) + M_2PI) % M_2PI;
    }
}
