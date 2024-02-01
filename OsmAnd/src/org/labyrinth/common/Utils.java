package org.labyrinth.common;

import android.util.Pair;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
}
