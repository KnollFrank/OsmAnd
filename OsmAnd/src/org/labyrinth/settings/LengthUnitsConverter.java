package org.labyrinth.settings;

class LengthUnitsConverter {

    private static final double CENTIMETRES_PER_FEET = 30.48;
    private static final int INCHES_PER_FEET = 12;
    private static final double CENTIMETRES_PER_INCH = CENTIMETRES_PER_FEET / INCHES_PER_FEET;

    public static double feetAndInches2Centimetres(final int feet, final double inches) {
        return CENTIMETRES_PER_FEET * feet + CENTIMETRES_PER_INCH * inches;
    }

    public static FeetAndInches centimetres2FeetAndInches(final double cm) {
        final double feet = cm / CENTIMETRES_PER_FEET;
        final int feetFloor = (int) Math.floor(feet);
        return new FeetAndInches(
                feetFloor,
                (feet - feetFloor) * INCHES_PER_FEET);
    }
}
