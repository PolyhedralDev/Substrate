package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;

public class EphemeralValue implements Value {
    private final Signature signature;
    private final int argument;

    public EphemeralValue(Signature signature, int argument) {
        this.signature = signature;
        this.argument = argument;
    }

    @Override
    public void load(MethodVisitor visitor, BuildData data) {
        System.out.println("Referencing value " + argument + " with op " + reference().getType(0) + ", " + hashCode());
        visitor.visitVarInsn(reference().getType(0).loadInsn(), argument);
    }

    @Override
    public Signature reference() {
        return signature;
    }
}
