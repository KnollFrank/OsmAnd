package org.labyrinth.coordinate;

import java.io.Serializable;
import java.util.Objects;

public class Angle implements Serializable {

    private static final double M_2PI = Math.PI * 2;

    public enum Unit {

        DEGREES, RADIANS
    }

    private final double angleInRadians;

    public static final Angle ZERO = new Angle(0, Unit.DEGREES);

    public Angle(final double angle, final Unit unit) {
        this.angleInRadians = getAngleInRadians(angle, unit);
    }

    private double getAngleInRadians(final double angle, final Unit unit) {
        switch (unit) {
            case DEGREES:
                return Math.toRadians(angle);
            case RADIANS:
                return angle;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Angle wrap0To360Degrees() {
        if (0 <= angleInRadians && angleInRadians < M_2PI) {
            return this;
        }

        return new Angle(((angleInRadians % M_2PI) + M_2PI) % M_2PI, Unit.RADIANS);
    }

    public double to(final Unit unit) {
        switch (unit) {
            case DEGREES:
                return Math.toDegrees(angleInRadians);
            case RADIANS:
                return angleInRadians;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Angle add(final Angle other) {
        return new Angle(this.angleInRadians + other.angleInRadians, Unit.RADIANS);
    }

    public Angle sub(final Angle other) {
        return new Angle(this.angleInRadians - other.angleInRadians, Unit.RADIANS);
    }

    public Angle get0To180DegreesDifferenceTo(final Angle other) {
        // BEGIN: performance optimization
        if (this.equals(other)) {
            return Angle.ZERO;
        }
        // END: performance optimization
        // this original line "final double angle0To360Degrees = this.sub(other).wrap0To360Degrees().toDegrees();"
        // leads to build error "com.android.tools.r8.errors.d: Unexpected non-trivial phi in method eligible for class inlining"
        final double angle0To360Degrees = Math.toDegrees(this.sub(other).wrap0To360Degrees().angleInRadians);
        final double angle0To180Degrees = angle0To360Degrees > 180 ? 360 - angle0To360Degrees : angle0To360Degrees;
        return new Angle(angle0To180Degrees, Unit.DEGREES);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Angle angle = (Angle) o;
        return Double.compare(angle.angleInRadians, angleInRadians) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(angleInRadians);
    }

    @Override
    public String toString() {
        return "Angle{angle = " + to(Unit.DEGREES) + "°}";
    }
}
