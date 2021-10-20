package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.value.Value;

public interface ValueInterceptor {
    void fetch(String id, BuildData source);
}
