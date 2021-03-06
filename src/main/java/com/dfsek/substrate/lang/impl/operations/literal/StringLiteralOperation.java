package com.dfsek.substrate.lang.impl.operations.literal;

import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
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
