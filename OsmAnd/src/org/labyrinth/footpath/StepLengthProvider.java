package org.labyrinth.footpath;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class StepLengthProvider {

    public static Quantity<Length> getStepLength(final Quantity<Length> height) {
        return height.multiply(0.415f);
    }
}
