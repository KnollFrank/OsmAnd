package org.labyrinth.common;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class ListUtils {

    public static <T> Optional<T> getStart(final List<T> ts) {
        return getElement(ts, 0);
    }

    public static <T> Optional<T> getEnd(final List<T> ts) {
        return getElement(ts, ts.size() - 1);
    }

    private static <T> Optional<T> getElement(final List<T> ts, final int index) {
        return 0 <= index && index < ts.size() ? Optional.of(ts.get(index)) : Optional.empty();
    }

    public static <T> int[] indexesOf(final List<T> haystack, final T needle) {
        return IntStream
                .range(0, haystack.size())
                .filter(i -> haystack.get(i).equals(needle))
                .toArray();
    }

    public static <T> List<T> getSublist(final List<T> ts, final int fromIndex) {
        return ts.subList(fromIndex, ts.size());
    }

    public static <T> boolean isValidIndex(final List<T> ts, final int index) {
        return 0 <= index && index < ts.size();
    }
}
