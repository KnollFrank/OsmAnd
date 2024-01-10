package org.labyrinth.common;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.naturalOrder;

// taken from https://gist.github.com/pholser/9ab05316d36d51b193a2
public class Functions {

    private Functions() {
        throw new UnsupportedOperationException();
    }

    public static <T, R extends Comparable<? super R>>
    Optional<T> argMin(
            Stream<? extends T> s,
            Function<? super T, ? extends R> fn) {

        return argMin(s, fn, naturalOrder());
    }

    public static <T, R>
    Optional<T> argMin(
            Stream<? extends T> s,
            Function<? super T, ? extends R> fn,
            Comparator<? super R> c) {

        return argMax(s, fn, reverseOrder(c));
    }

    public static <T, R extends Comparable<? super R>>
    Optional<Integer> argMin(
            List<? extends T> s,
            Function<? super T, ? extends R> fn) {

        return argMin(s, fn, naturalOrder());
    }

    public static <T, R>
    Optional<Integer> argMin(
            List<? extends T> s,
            Function<? super T, ? extends R> fn,
            Comparator<? super R> c) {

        return argMax(s, fn, reverseOrder(c));
    }

    public static <T, R extends Comparable<? super R>>
    Optional<T> argMax(
            Stream<? extends T> s,
            Function<? super T, ? extends R> fn) {

        return argMax(s, fn, naturalOrder());
    }

    public static <T, R>
    Optional<T> argMax(
            Stream<? extends T> s,
            Function<? super T, ? extends R> fn,
            Comparator<? super R> c) {

        Optional<? extends SimpleEntry<? extends T, ? extends R>> maxPair =
                s.map(item -> new SimpleEntry<>(item, fn.apply(item)))
                        .max((fst, snd) -> c.compare(fst.getValue(), snd.getValue()));
        return maxPair.map(SimpleEntry::getKey);
    }

    public static <T, R extends Comparable<? super R>>
    Optional<Integer> argMax(
            List<? extends T> s,
            Function<? super T, ? extends R> fn) {

        return argMax(s, fn, naturalOrder());
    }

    public static <T, R>
    Optional<Integer> argMax(
            List<? extends T> s,
            Function<? super T, ? extends R> fn,
            Comparator<? super R> c) {

        Stream<SimpleEntry<Integer, T>> withIndex =
                IntStream.range(0, s.size()).mapToObj(i -> new SimpleEntry<>(i, s.get(i)));
        Function<SimpleEntry<Integer, T>, R> fnPrime = e -> fn.apply(e.getValue());

        Optional<SimpleEntry<Integer, T>> maxWithIndex = argMax(withIndex, fnPrime, c);
        return maxWithIndex.map(SimpleEntry::getKey);
    }
}
