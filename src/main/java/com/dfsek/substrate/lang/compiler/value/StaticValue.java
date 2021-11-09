package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;

public class StaticValue implements Value {
    private final Signature ref;
    private final String type;
    private final int field;

    public StaticValue(Signature ref, String type, int field) {
        this.ref = ref;
        this.type = type;
        this.field = field;

    }

    @Override
    public Signature reference() {
        return ref;
    }

    @Override
    public void load(MethodVisitor visitor, BuildData data) {
        visitor.visitFieldInsn(GETSTATIC,
                data.getClassName(),
                "val" + field,
                type);
    }
}
