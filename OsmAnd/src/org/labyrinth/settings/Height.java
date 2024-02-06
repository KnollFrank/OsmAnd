package org.labyrinth.settings;

import org.labyrinth.common.MeasureUtils;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

class Height {

    public final LengthUnit lengthUnit;
    public final double heightInCM;

    public Height(final LengthUnit lengthUnit, final double heightInCM) {
        this.lengthUnit = lengthUnit;
        this.heightInCM = heightInCM;
    }

    public static Height fromQuantity(final Quantity<Length> height) {
        return new Height(
                LengthUnit.CENTIMETRE,
                MeasureUtils.toCentiMetres(height));
    }

    public Quantity<Length> toQuantity() {
        return MeasureUtils.fromCentiMetres(this.heightInCM);
    }
}
