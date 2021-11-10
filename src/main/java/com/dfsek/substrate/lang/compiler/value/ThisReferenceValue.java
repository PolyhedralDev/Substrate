package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;

public class ThisReferenceValue implements Value{
    private final Signature ref;

    public ThisReferenceValue(Signature ref) {
        this.ref = ref;
    }

    @Override
    public Signature reference() {
        return ref;
    }

    @Override
    public void load(MethodVisitor visitor, BuildData data) {
        if(!ref.isSimple()) {
            visitor.visitVarInsn(ALOAD, 0);
        } else {
            visitor.visitVarInsn(reference().getType(0).loadInsn(), 0);
        }
    }
}
