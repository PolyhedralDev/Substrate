package com.dfsek.terrascript.lang.impl.operations.variable.declaration;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.tokenizer.Position;
import com.dfsek.terrascript.tokenizer.Token;

public class NumberVariableDeclarationOperation extends VariableDeclarationOperation {
    public NumberVariableDeclarationOperation(Token id, Position position) {
        super(position, id);
    }

    @Override
    public ReturnType getVariableType() {
        return ReturnType.NUM;
    }
}
