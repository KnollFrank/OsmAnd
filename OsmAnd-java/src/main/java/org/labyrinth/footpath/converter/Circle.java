package org.labyrinth.footpath.converter;

import org.labyrinth.common.MeasureUtils;
import org.labyrinth.coordinate.Geodetic;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

public class Circle {

    public final Geodetic center;
    public final Quantity<Length> radius;

    public Circle(final Geodetic center, final Quantity<Length> radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean contains(final Geodetic geodetic) {
        return MeasureUtils.isLessOrEqual(center.getDistanceTo(geodetic), radius);
    }
}
