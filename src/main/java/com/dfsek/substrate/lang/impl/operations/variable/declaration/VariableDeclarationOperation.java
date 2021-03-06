package com.dfsek.substrate.lang.impl.operations.variable.declaration;

import com.dfsek.substrate.lang.impl.ScriptBuildData;
import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public abstract class VariableDeclarationOperation implements Operation {
    private final Position position;
    private final Operation value;
    protected VariableDeclarationOperation(Position position, Token id, Operation value) {
        this.position = position;
        this.value = value;
        this.id = id;
    }

    public abstract ReturnType getVariableType();

    private final Token id;

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        ((ScriptBuildData) data).register(id, getVariableType());
        if(value != null) {
            value.apply(visitor, data);
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
