package com.dfsek.terrascript.lang.impl.operations.variable.declaration;

import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.tokenizer.Position;
import com.dfsek.terrascript.tokenizer.Token;

public class BooleanVariableDeclarationOperation extends VariableDeclarationOperation {
    public BooleanVariableDeclarationOperation(Token id, Position position, Operation value) {
        super(position, id, value);
    }

    @Override
    public ReturnType getVariableType() {
        return ReturnType.BOOL;
    }
}
