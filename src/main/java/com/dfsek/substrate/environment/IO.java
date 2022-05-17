package com.dfsek.substrate.environment;

public interface IO<T extends Environment> {
    void apply(T env);
}
