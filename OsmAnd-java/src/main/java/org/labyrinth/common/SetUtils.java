package org.labyrinth.common;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetUtils {

    public static <T> Set<T> union(final Collection<Set<T>> sets) {
        return union(sets.stream());
    }

    public static <T> Set<T> union(final Set<T>... sets) {
        return union(Stream.of(sets));
    }

    private static <T> Set<T> union(final Stream<Set<T>> streamOfSets) {
        return streamOfSets.flatMap(Set::stream).collect(Collectors.toSet());
    }

    public static <T> T popAnyOrElseNull(final Set<T> ts) {
        final Optional<T> t = ts.stream().findAny();
        t.map(ts::remove);
        return t.orElse(null);
    }
}
