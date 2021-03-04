package com.dfsek.terrascript.lang.impl.operations.variable.assignment;

import com.dfsek.terrascript.lang.internal.Operation;

public abstract class VariableAssignmentOperation implements Operation {
    protected final Operation value;
    protected final String id;

    protected VariableAssignmentOperation(Operation value, String id) {
        this.value = value;
        this.id = id;
    }
}
