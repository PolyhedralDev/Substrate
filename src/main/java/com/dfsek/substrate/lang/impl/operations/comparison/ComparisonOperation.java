package com.dfsek.substrate.lang.impl.operations.comparison;

import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.tokenizer.Position;

public abstract class ComparisonOperation implements Operation {
    protected final Operation left;
    protected final Operation right;
    protected final Position position;

    protected ComparisonOperation(Operation left, Operation right, Position position) {
        this.left = left;
        this.right = right;
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public ReturnType getType() {
        return ReturnType.BOOL;
    }
}
