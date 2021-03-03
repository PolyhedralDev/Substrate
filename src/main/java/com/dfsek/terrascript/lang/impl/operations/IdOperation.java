package com.dfsek.terrascript.lang.impl.operations;

import com.dfsek.terrascript.lang.internal.Operation;

public class IdOperation implements Operation {
    private final String id;

    public IdOperation(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
