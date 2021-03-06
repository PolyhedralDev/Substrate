package com.dfsek.substrate.lang.impl.operations.comparison.number;

import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class GreaterThanOperationNumber extends NumberComparisonOperation {
    public GreaterThanOperationNumber(Operation left, Operation right, Position position) {
        super(left, right, position);
    }

    @Override
    public void applyOperation(MethodVisitor visitor, BuildData data) throws ParseException {
        visitor.visitInsn(DCMPL);
        Label equal = new Label();
        Label notEqual = new Label();
        visitor.visitJumpInsn(IFLE, notEqual);
        visitor.visitInsn(ICONST_1);
        visitor.visitJumpInsn(GOTO, equal);
        visitor.visitLabel(notEqual);
        visitor.visitInsn(ICONST_0);
        visitor.visitLabel(equal);
    }
}
