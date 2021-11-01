package com.dfsek.substrate.lang.compiler;

import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Value;

public class PrimitiveValue implements Value {
    private final Signature reference;

    public PrimitiveValue(Signature reference) {
        this.reference = reference;
    }

    @Override
    public Signature reference() {
        return reference;
    }

    @Override
    public boolean ephemeral() {
        return false;
    }
}
