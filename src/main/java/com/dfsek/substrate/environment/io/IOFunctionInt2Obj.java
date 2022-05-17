package com.dfsek.substrate.environment.io;

import com.dfsek.substrate.environment.Environment;

public interface IOFunctionInt2Obj<B> {
    B apply(Environment environment, int in);
}
