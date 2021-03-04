package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.tokenizer.Position;

public abstract class LiteralOperation<T> implements Operation {
    protected final T literal;
    protected final Position position;

    protected LiteralOperation(T literal, Position position) {
        this.literal = literal;
        this.position = position;
    }

    public T getLiteral() {
        return literal;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
