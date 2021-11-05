package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.compiler.build.BuildData;

public interface Typed {
    Signature reference(BuildData data);
}
