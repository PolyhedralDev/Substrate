package com.dfsek.substrate.environment.io;

import com.dfsek.substrate.environment.Environment;

public interface IOFunction1<A, B> {
    B apply(Environment environment, A in);
}
