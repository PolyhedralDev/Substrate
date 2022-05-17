package com.dfsek.substrate.environment;

import java.util.function.Function;

public interface IO<V, T extends Environment> {
    V apply(T env);

    static <T, U, E extends Environment> IO<U, E> bind(IO<T, E> io, Function<T, IO<U, E>> function) {
        return env -> function.apply(io.apply(env)).apply(env);
    }
}
