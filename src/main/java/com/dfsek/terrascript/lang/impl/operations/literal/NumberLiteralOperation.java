package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;

public class NumberLiteralOperation extends LiteralOperation<Double> {
    public NumberLiteralOperation(double literal) {
        super(literal);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        visitor.visitLdcInsn(literal);
    }
}
