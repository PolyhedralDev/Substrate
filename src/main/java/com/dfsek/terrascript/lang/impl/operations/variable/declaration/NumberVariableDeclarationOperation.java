package com.dfsek.terrascript.lang.impl.operations.variable.declaration;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class NumberVariableDeclarationOperation extends VariableDeclarationOperation {
    public NumberVariableDeclarationOperation(Token id) {
        super(id);
    }

    @Override
    public ScriptBuildData.VariableType getType() {
        return ScriptBuildData.VariableType.NUM;
    }
}
