package org.labyrinth.coordinate;

import java.io.Serializable;
import java.util.Objects;

import static math.geom2d.Angle2D.M_2PI;
import static org.labyrinth.coordinate.Unit.DEGREES;
import static org.labyrinth.coordinate.Unit.RADIANS;

public class Angle implements Serializable {

    private final double angleInRadians;

    public static final Angle ZERO = new Angle(0, DEGREES);

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

        return new Angle(((angleInRadians % M_2PI) + M_2PI) % M_2PI, RADIANS);
    }

    public double toDegrees() {
        return Math.toDegrees(angleInRadians);
    }

    public double toRadians() {
        return angleInRadians;
    }

    public Angle add(final Angle other) {
        return new Angle(this.angleInRadians + other.angleInRadians, RADIANS);
    }

    public Angle sub(final Angle other) {
        return new Angle(this.angleInRadians - other.angleInRadians, RADIANS);
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
        return new Angle(angle0To180Degrees, DEGREES);
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
        return "Angle{angle = " + toDegrees() + "°}";
    }
}
