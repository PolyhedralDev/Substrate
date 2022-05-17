package com.dfsek.substrate.environment;

import com.dfsek.substrate.environment.io.IOFunctionInt2Obj;
import com.dfsek.substrate.environment.io.IOFunctionNum2Obj;
import com.dfsek.substrate.environment.io.IOFunctionObj2Obj;

public interface IO<V, T extends Environment> {
    V apply(T env);

    static <T, U, E extends Environment> IO<U, E> bind(Environment environment, IO<T, E> io, IOFunctionObj2Obj<T, IO<U, E>> function) {
        return env -> function.apply(environment, io.apply(env)).apply(env);
    }

    static <U, E extends Environment> IO<U, E> bind(Environment environment, IO<Integer, E> io, IOFunctionInt2Obj<IO<U, E>> function) {
        return env -> function.apply(environment, io.apply(env)).apply(env);
    }

    static <U, E extends Environment> IO<U, E> bind(Environment environment, IO<Double, E> io, IOFunctionNum2Obj<IO<U, E>> function) {
        return env -> function.apply(environment, io.apply(env)).apply(env);
    }
}
