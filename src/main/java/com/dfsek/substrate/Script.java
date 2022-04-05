package com.dfsek.substrate;

public interface Script<P extends Record, R extends Record> {
    R execute(P parameters, Environment environment);
}
