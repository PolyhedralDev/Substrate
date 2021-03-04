package com.dfsek.terrascript.lang.impl.operations.variable.declaration;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.tokenizer.Token;

public class BooleanVariableDeclarationOperation extends VariableDeclarationOperation {
    public BooleanVariableDeclarationOperation(Token id) {
        super(id);
    }

    @Override
    public ScriptBuildData.VariableType getType() {
        return ScriptBuildData.VariableType.BOOL;
    }
}
