package com.dfsek.substrate.lang.compiler;

import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Value;

public class PrimitiveValue implements Value {
    private final Signature signature;

    public PrimitiveValue(Signature signature) {
        this.signature = signature;
    }

    @Override
    public Signature returnType() {
        return signature;
    }

    @Override
    public Signature reference() {
        return signature;
    }

    @Override
    public boolean ephemeral() {
        return false;
    }
}
