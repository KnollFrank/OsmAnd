package org.labyrinth.common;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.Units.METRE;

import java.util.stream.Stream;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import tec.units.ri.quantity.Quantities;

public class MeasureUtils {

    public static double divide(final Quantity<Length> dividend, final Quantity<Length> divisor) {
        final Unit<Length> commonUnit4Division = METRE;
        return dividend.to(commonUnit4Division)
                .divide(divisor.to(commonUnit4Division))
                .getValue().doubleValue();
    }

    // length1 >= length2
    public static boolean isGreaterOrEqual(final Quantity<Length> length1, final Quantity<Length> length2) {
        return length1.subtract(length2).getValue().doubleValue() >= 0.0;
    }

    // length1 <= length2
    public static boolean isLessOrEqual(final Quantity<Length> length1, final Quantity<Length> length2) {
        return isGreaterOrEqual(length2, length1);
    }

    public static Quantity<Length> abs(final Quantity<Length> length) {
        return Quantities.getQuantity(
                Math.abs(length.getValue().doubleValue()),
                length.getUnit());
    }

    public static Quantity<Length> round(final Quantity<Length> lengthQuantity, final Unit<Length> unit) {
        return getQuantity(
                Math.round(lengthQuantity.to(unit).getValue().doubleValue()),
                unit);
    }

    public static Quantity<Length> sum(final Stream<Quantity<Length>> lens) {
        return lens.reduce(getQuantity(0.0, METRE), Quantity::add);
    }

    public static Quantity<Length> min(final Quantity<Length> length1, final Quantity<Length> length2) {
        return isLessOrEqual(length1, length2) ? length1 : length2;
    }

    public static Quantity<Length> max(final Quantity<Length> length1, final Quantity<Length> length2) {
        return isGreaterOrEqual(length1, length2) ? length1 : length2;
    }

    public static Quantity<Length> clampValueToMinMax(final Quantity<Length> value,
                                                      final Quantity<Length> min,
                                                      final Quantity<Length> max) {
        return MeasureUtils.max(min, MeasureUtils.min(max, value));
    }

    public static double toMetres(final Quantity<Length> length) {
        return length.to(METRE).getValue().doubleValue();
    }

    public static Quantity<Length> fromCentiMetres(final double lengthInCentiMetres) {
        return getQuantity(lengthInCentiMetres, CENTI(METRE));
    }

    public static double toCentiMetres(final Quantity<Length> length) {
        return length.to(CENTI(METRE)).getValue().doubleValue();
    }
}
