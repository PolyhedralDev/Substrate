package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.Operation;

public abstract class LiteralOperation<T> implements Operation {
    protected final T literal;

    protected LiteralOperation(T literal) {
        this.literal = literal;
    }

    public T getLiteral() {
        return literal;
    }
}
