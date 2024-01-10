package org.labyrinth.common;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MapUtils {

    public static <K, V> Map<K, V> filterMapByKey(final Map<K, V> map, final Predicate<K> keyPredicate) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> keyPredicate.test(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> filterMapByValue(final Map<K, V> map, final Predicate<V> valuePredicate) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> valuePredicate.test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V, U> Map<K, U> transformValuesOfMap(final Map<K, V> map,
                                                           final Function<V, U> valueTransformer) {
        return transformValuesOfMapBi(map, (key, val) -> valueTransformer.apply(val));
    }

    public static <K, V, U> Map<K, U> transformValuesOfMapBi(final Map<K, V> map,
                                                             final BiFunction<K, V, U> valueTransformer) {
        return map
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> valueTransformer.apply(entry.getKey(), entry.getValue())));
    }
}
