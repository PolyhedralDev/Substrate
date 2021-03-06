package com.dfsek.substrate.lang.impl.operations.variable.declaration;

import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

public class StringVariableDeclarationOperation extends VariableDeclarationOperation{
    public StringVariableDeclarationOperation(Token id, Position position, Operation value) {
        super(position, id, value);
    }

    @Override
    public ReturnType getVariableType() {
        return ReturnType.STR;
    }
}
