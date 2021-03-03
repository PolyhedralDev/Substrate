package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.Operation;

public class StringLiteralOperation implements Operation {
    private final String literal;

    public StringLiteralOperation(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }
}
