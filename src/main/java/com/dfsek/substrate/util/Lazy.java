package com.dfsek.substrate.util;

import java.util.function.Supplier;

public final class Lazy<T> {
    private final Supplier<T> value;
    private T result;
    private volatile boolean calc = false;

    private Lazy(Supplier<T> value) {
        this.value = value;
    }

    public static <T> Lazy<T> of(Supplier<T> value) {
        return new Lazy<>(value);
    }

    public T get() {
        if (calc) return result;
        result = value.get();
        calc = true;
        return result;
    }
}
