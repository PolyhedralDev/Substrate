package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;

public class ShadowValue implements Value {
    private final Signature signature;
    private final int field;

    public ShadowValue(Signature signature, int field) {
        this.signature = signature;
        this.field = field;
    }

    @Override
    public void load(MethodVisitor visitor, BuildData data) {
        visitor.visitVarInsn(ALOAD, 0);
        System.out.println(data);
        System.out.println(data.getClassName());
        visitor.visitFieldInsn(GETFIELD,
                data.getClassName(),
                "scope" + field,
                reference().internalDescriptor());
    }

    @Override
    public Signature reference() {
        return signature;
    }
}
