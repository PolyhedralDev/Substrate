package com.dfsek.terrascript.lang.impl.operations.variable.assignment;

import com.dfsek.terrascript.lang.internal.Operation;

public abstract class VariableAssignmentOperation implements Operation {
    private final Operation value;

    protected VariableAssignmentOperation(Operation value) {
        this.value = value;
    }
}
