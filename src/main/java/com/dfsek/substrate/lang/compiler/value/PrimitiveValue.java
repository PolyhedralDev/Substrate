package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;

public class PrimitiveValue implements Value {
    private final Signature reference;
    private final int offset;

    @Override
    public void load(MethodVisitor visitor, BuildData data) {
        System.out.println("offset: " + offset);
        if(!reference.isSimple()) {
            visitor.visitVarInsn(ALOAD, offset);
        } else {
            visitor.visitVarInsn(reference().getType(0).loadInsn(), offset);
        }
    }

    public PrimitiveValue(Signature reference, int offset) {
        this.reference = reference;
        this.offset = offset;
    }

    @Override
    public Signature reference() {
        return reference;
    }

}
