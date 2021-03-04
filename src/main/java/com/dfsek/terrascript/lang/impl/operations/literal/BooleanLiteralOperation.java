package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class BooleanLiteralOperation extends LiteralOperation<Boolean> {

    public BooleanLiteralOperation(boolean literal, Position position) {
        super(literal, position);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(literal) visitor.visitInsn(ICONST_1);
        else visitor.visitInsn(ICONST_0);
    }

    @Override
    public ReturnType getType() {
        return ReturnType.BOOL;
    }
}
