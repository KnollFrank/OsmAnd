package org.labyrinth.common;

import tec.units.ri.function.NaturalOrder;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import java.util.Comparator;
import java.util.stream.Stream;

import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.Units.METRE;

public class MeasureUtils {

    public static final Comparator<Quantity<Length>> COMPARATOR = new NaturalOrder<>();

    public static double divide(final Quantity<Length> dividend, final Quantity<Length> divisor) {
        final Unit<Length> commonUnit4Division = METRE;
        return dividend.to(commonUnit4Division)
                .divide(divisor.to(commonUnit4Division))
                .getValue().doubleValue();
    }

    // length1 > length2
    public static boolean isGreaterThan(final Quantity<Length> length1, final Quantity<Length> length2) {
        return length1.subtract(length2).getValue().doubleValue() > 0.0;
    }

    // length1 >= length2
    public static boolean isGreaterOrEqual(final Quantity<Length> length1, final Quantity<Length> length2) {
        return length1.subtract(length2).getValue().doubleValue() >= 0.0;
    }

    // length1 <= length2
    public static boolean isLessOrEqual(final Quantity<Length> length1, final Quantity<Length> length2) {
        return isGreaterOrEqual(length2, length1);
    }

    // length1 < length2
    public static boolean isLessThan(final Quantity<Length> length1, final Quantity<Length> length2) {
        return isGreaterThan(length2, length1);
    }

    public static Quantity<Length> abs(final Quantity<Length> length) {
        return Quantities.getQuantity(
                Math.abs(length.getValue().doubleValue()),
                length.getUnit());
    }

    public static double toMeters(final Quantity<Length> length) {
        return length.to(METRE).getValue().doubleValue();
    }

    public static Quantity<Length> round(final Quantity<Length> lengthQuantity, final Unit<Length> unit) {
        return getQuantity(
                Math.round(lengthQuantity.to(unit).getValue().doubleValue()),
                unit);
    }

    public static Quantity<Length> sum(final Stream<Quantity<Length>> lens) {
        return lens.reduce(getQuantity(0.0, METRE), Quantity::add);
    }
}
