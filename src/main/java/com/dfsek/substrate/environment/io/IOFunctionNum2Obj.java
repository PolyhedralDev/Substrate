package com.dfsek.substrate.environment.io;

import com.dfsek.substrate.environment.Environment;

public interface IOFunctionNum2Obj<B> {
    B apply(Environment environment, double in);
}
