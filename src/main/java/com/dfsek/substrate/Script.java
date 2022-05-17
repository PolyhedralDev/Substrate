package com.dfsek.substrate;

import com.dfsek.substrate.environment.Environment;

public interface Script<P extends Record, R extends Record> {
    R execute(P parameters, Environment environment);
}
