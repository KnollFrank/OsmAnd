package org.labyrinth.settings;

class Spinner2LengthUnitConverter {

    public static LengthUnit itemPos2LengthUnit(final int itemPos) {
        return itemPos == 0 ? LengthUnit.CENTIMETRE : LengthUnit.FEET_AND_INCHES;
    }

    public static int lengthUnit2ItemPos(final LengthUnit lengthUnit) {
        return lengthUnit == LengthUnit.CENTIMETRE ? 0 : 1;
    }
}
