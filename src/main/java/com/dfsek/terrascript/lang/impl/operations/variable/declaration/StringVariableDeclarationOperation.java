package com.dfsek.terrascript.lang.impl.operations.variable.declaration;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.tokenizer.Token;

public class StringVariableDeclarationOperation extends VariableDeclarationOperation{
    public StringVariableDeclarationOperation(Token id) {
        super(id);
    }

    @Override
    public ScriptBuildData.VariableType getType() {
        return ScriptBuildData.VariableType.STRING;
    }
}
