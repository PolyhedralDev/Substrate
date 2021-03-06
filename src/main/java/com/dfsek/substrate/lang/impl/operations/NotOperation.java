package com.dfsek.substrate.lang.impl.operations;

import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class NotOperation implements Operation {
    private final Position position;

    public NotOperation(Position position) {
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        Label t = new Label();
        Label f = new Label();
        visitor.visitJumpInsn(IFNE, f);
        visitor.visitInsn(ICONST_1);
        visitor.visitJumpInsn(GOTO, t);
        visitor.visitLabel(f);
        visitor.visitInsn(ICONST_0);
        visitor.visitLabel(t);
    }

    @Override
    public ReturnType getType() {
        return ReturnType.BOOL;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
