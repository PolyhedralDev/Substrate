package com.dfsek.terrascript.lang.impl.operations.variable.assignment;

import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.tokenizer.Position;

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
