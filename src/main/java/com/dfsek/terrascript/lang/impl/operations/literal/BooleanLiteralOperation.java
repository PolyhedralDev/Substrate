package com.dfsek.terrascript.lang.impl.operations.literal;

public class BooleanLiteralOperation extends LiteralOperation {
    private final boolean literal;

    public BooleanLiteralOperation(boolean value) {
        this.literal = value;
    }

    public boolean getLiteral() {
        return literal;
    }
}
