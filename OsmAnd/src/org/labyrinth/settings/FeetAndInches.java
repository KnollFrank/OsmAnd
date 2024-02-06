package org.labyrinth.settings;

import static org.labyrinth.settings.LengthUnitsConverter.feetAndInches2Centimetres;

import java.util.StringJoiner;

class FeetAndInches implements Comparable<FeetAndInches> {

    public final int feet;
    public final double inches;

    public FeetAndInches(final int feet, final double inches) {
        this.feet = feet;
        this.inches = inches;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FeetAndInches.class.getSimpleName() + "[", "]")
                .add("feet=" + feet)
                .add("inches=" + inches)
                .toString();
    }

    @Override
    public int compareTo(final FeetAndInches other) {
        return Double.compare(
                feetAndInches2Centimetres(this.feet, this.inches),
                feetAndInches2Centimetres(other.feet, other.inches));
    }
}
