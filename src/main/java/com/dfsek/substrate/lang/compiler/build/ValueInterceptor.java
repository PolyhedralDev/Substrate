package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.value.Value;

public interface ValueInterceptor {
    void register(String id, Value value);
}
