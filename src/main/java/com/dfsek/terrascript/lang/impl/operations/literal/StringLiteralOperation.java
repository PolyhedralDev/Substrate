package com.dfsek.terrascript.lang.impl.operations.literal;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class StringLiteralOperation extends LiteralOperation<String> {
    private final String literal;

    public StringLiteralOperation(String literal, Position position) {
        super(literal, position);
        this.literal = literal;
    }
    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        visitor.visitLdcInsn(literal);
    }

    @Override
    public ReturnType getType() {
        return ReturnType.STR;
    }
}
