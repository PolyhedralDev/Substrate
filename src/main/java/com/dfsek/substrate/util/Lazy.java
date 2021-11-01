package com.dfsek.substrate.util;

import java.util.function.Supplier;

public final class Lazy<T> {
    private final Supplier<T> value;
    private T result;
    private boolean calc = false;

    public static <T> Lazy<T> of(Supplier<T> value) {
        return new Lazy<>(value);
    }

    private Lazy(Supplier<T> value) {
        this.value = value;
    }

    public T get() {
        if(calc) return result;
        result = value.get();
        calc = true;
        return result;
    }

    public void invalidate() {
        calc = false;
    }
}
