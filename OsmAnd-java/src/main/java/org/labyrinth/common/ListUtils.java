package org.labyrinth.common;

import org.jgrapht.alg.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListUtils {

    public static <T> boolean isValidIndex(final List<T> ts, final int index) {
        return 0 <= index && index < ts.size();
    }

    public static <T> Optional<T> getStart(final List<T> ts) {
        return getElement(ts, 0);
    }

    public static <T> Optional<T> getEnd(final List<T> ts) {
        return getElement(ts, ts.size() - 1);
    }

    private static <T> Optional<T> getElement(final List<T> ts, final int index) {
        return 0 <= index && index < ts.size() ? Optional.of(ts.get(index)) : Optional.empty();
    }

    public static <A, B> List<A> getFirsts(final List<Pair<A, B>> pairs) {
        return pairs
                .stream()
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

    public static <A, B> List<B> getSeconds(final List<Pair<A, B>> pairs) {
        return pairs
                .stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());
    }
}
