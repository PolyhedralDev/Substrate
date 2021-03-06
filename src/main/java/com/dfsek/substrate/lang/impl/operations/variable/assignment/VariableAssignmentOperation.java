package com.dfsek.substrate.lang.impl.operations.variable.assignment;

import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.tokenizer.Position;

public abstract class VariableAssignmentOperation implements Operation {
    protected final Operation value;
    protected final String id;
    protected final Position position;

    protected VariableAssignmentOperation(Operation value, String id, Position position) {
        this.value = value;
        this.id = id;
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
