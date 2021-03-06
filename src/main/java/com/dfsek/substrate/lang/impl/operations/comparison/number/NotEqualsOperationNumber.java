package com.dfsek.substrate.lang.impl.operations.comparison.number;

import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class NotEqualsOperationNumber extends NumberComparisonOperation {
    public NotEqualsOperationNumber(Operation left, Operation right, Position position) {
        super(left, right, position);
    }

    @Override
    public void applyOperation(MethodVisitor visitor, BuildData data) {
        visitor.visitInsn(DCMPL);
        Label equal = new Label();
        Label notEqual = new Label();
        visitor.visitJumpInsn(IFNE, notEqual);
        visitor.visitInsn(ICONST_0);
        visitor.visitJumpInsn(GOTO, equal);
        visitor.visitLabel(notEqual);
        visitor.visitInsn(ICONST_1);
        visitor.visitLabel(equal);
    }
}
