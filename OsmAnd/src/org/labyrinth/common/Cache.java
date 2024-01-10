package org.labyrinth.common;

import java.util.function.Supplier;

public class Cache<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T value = null;

    public Cache(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }
}
