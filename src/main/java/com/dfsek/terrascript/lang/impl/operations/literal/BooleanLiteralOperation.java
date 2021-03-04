package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;

public class BooleanLiteralOperation extends LiteralOperation<Boolean> {

    public BooleanLiteralOperation(boolean literal) {
        super(literal);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(literal) visitor.visitInsn(ICONST_1);
        else visitor.visitInsn(ICONST_0);
    }
}
