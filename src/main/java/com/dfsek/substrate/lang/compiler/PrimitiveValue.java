package com.dfsek.substrate.lang.compiler;

public class PrimitiveValue implements Value {
    private final Signature signature;
    private final String name;

    public PrimitiveValue(Signature signature, String name) {
        this.signature = signature;
        this.name = name;
    }

    @Override
    public Signature type() {
        return signature;
    }
}
