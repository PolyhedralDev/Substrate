package com.dfsek.terrascript.lang.impl.operations.variable.declaration;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.tokenizer.Position;
import com.dfsek.terrascript.tokenizer.Token;

public class StringVariableDeclarationOperation extends VariableDeclarationOperation{
    public StringVariableDeclarationOperation(Token id, Position position) {
        super(position, id);
    }

    @Override
    public ScriptBuildData.VariableType getVariableType() {
        return ScriptBuildData.VariableType.STRING;
    }
}
