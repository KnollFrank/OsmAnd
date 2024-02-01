package org.labyrinth.common;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {

    public static <T> Stream<Pair<T, T>> getConsecutivePairs(final List<T> ts) {
        return getConsecutivePairs(0, ts.size() - 1)
                .map(i_iplus1 ->
                        Pair.create(
                                ts.get(i_iplus1.first),
                                ts.get(i_iplus1.second)));
    }

    public static Stream<Pair<Integer, Integer>> getConsecutivePairs(final int firstElementOfFirstPair,
                                                                     final int secondElementOfLastPair) {
        return IntStream
                .range(firstElementOfFirstPair, secondElementOfLastPair)
                .mapToObj(i -> Pair.create(i, i + 1));
    }

    public static float[] doubles2floats(final double[] doubles) {
        final float[] floats = new float[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            floats[i] = (float) doubles[i];
        }
        return floats;
    }

    public static double clampValueToMinMax(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static <T extends Comparable<T>> T clampValueToMinMax(final T value, final T min, final T max) {
        return value.compareTo(min) < 0 ?
                min :
                value.compareTo(max) > 0 ?
                        max :
                        value;
    }

    public static <T> Stream<T> asStream(final Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }
}
