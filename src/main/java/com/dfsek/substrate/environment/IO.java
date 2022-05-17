package com.dfsek.substrate.environment;

public interface IO<V, T extends Environment> {
    V apply(T env);
}
