package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Value;

public class EphemeralValue implements Value {
    private final Signature signature;

    public EphemeralValue(Signature signature) {
        this.signature = signature;
    }

    @Override
    public Signature reference() {
        return signature;
    }
}
