package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;

public class StringLiteralOperation extends LiteralOperation<String> {
    private final String literal;

    public StringLiteralOperation(String literal) {
        super(literal);
        this.literal = literal;
    }
    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        visitor.visitLdcInsn(literal);
    }
}
