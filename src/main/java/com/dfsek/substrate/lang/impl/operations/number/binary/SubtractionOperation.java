package com.dfsek.substrate.lang.impl.operations.number.binary;

import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class SubtractionOperation extends BinaryNumberOperation {
    public SubtractionOperation(Operation left, Operation right, Position position) {
        super(left, right, position);
    }

    @Override
    public void applyOperation(MethodVisitor visitor, BuildData data) {
        visitor.visitInsn(DSUB);
    }
}
