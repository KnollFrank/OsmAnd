package org.labyrinth.common;

import org.jgrapht.alg.util.Pair;

import java.util.Collection;
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
                        Pair.of(
                                ts.get(i_iplus1.getFirst()),
                                ts.get(i_iplus1.getSecond())));
    }

    public static Stream<Pair<Integer, Integer>> getConsecutivePairs(final int firstElementOfFirstPair,
                                                                     final int secondElementOfLastPair) {
        return IntStream
                .range(firstElementOfFirstPair, secondElementOfLastPair)
                .mapToObj(i -> Pair.of(i, i + 1));
    }

    public static <T> Stream<T> asStream(final Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }

    public static <T> T getAny(final Collection<T> ts) {
        return ts.stream().findAny().get();
    }
}
