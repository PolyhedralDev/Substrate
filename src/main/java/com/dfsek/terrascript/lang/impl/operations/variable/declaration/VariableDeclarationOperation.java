package com.dfsek.terrascript.lang.impl.operations.variable.declaration;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import com.dfsek.terrascript.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public abstract class VariableDeclarationOperation implements Operation {
    private final Position position;
    protected VariableDeclarationOperation(Position position, Token id) {
        this.position = position;
        this.id = id;
    }

    public abstract ScriptBuildData.VariableType getVariableType();

    private final Token id;

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        System.out.println("APP:" +id.getContent());
        ((ScriptBuildData) data).register(id, getVariableType());
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
