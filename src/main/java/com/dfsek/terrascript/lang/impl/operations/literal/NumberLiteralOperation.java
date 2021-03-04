package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class NumberLiteralOperation extends LiteralOperation<Double> {
    public NumberLiteralOperation(double literal, Position position) {
        super(literal, position);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        visitor.visitLdcInsn(literal);
    }

    @Override
    public ReturnType getType() {
        return ReturnType.NUM;
    }
}
