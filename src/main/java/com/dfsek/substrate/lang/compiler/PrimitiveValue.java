package com.dfsek.substrate.lang.compiler;

import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Value;

public class PrimitiveValue implements Value {
    private final Signature signature;
    private final String name;

    public PrimitiveValue(Signature signature, String name) {
        this.signature = signature;
        this.name = name;
    }

    @Override
    public Signature returnType() {
        return signature;
    }

    @Override
    public boolean ephemeral() {
        return false;
    }
}
