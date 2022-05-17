package com.dfsek.substrate.environment.io;

import com.dfsek.substrate.environment.Environment;

public interface IOFunctionObj2Obj<A, B> {
    B apply(Environment environment, A in);
}
