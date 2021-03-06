package com.dfsek.substrate.lang.impl.operations.comparison.string;

import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class EqualsOperationString extends StringComparisonOperation {
    public EqualsOperationString(Operation left, Operation right, Position position) {
        super(left, right, position);
    }

    @Override
    public void applyOperation(MethodVisitor visitor, BuildData data) {
        visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
        //visitor.visitInsn(POP);
    }
}
