package com.dfsek.terrascript.lang.impl.operations.number.binary;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class ModuloOperation extends BinaryNumberOperation{
    public ModuloOperation(Operation left, Operation right, Position position) {
        super(left, right, position);
    }

    @Override
    public void applyOperation(MethodVisitor visitor, BuildData data) {
        visitor.visitInsn(DREM);
    }
}
