package com.dfsek.terrascript.lang.impl.operations;

import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.tokenizer.Position;

public class IdOperation implements Operation {
    private final String id;
    private final Position position;

    public IdOperation(String id, Position position) {
        this.id = id;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
