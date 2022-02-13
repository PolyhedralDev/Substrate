package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;

public class PrimitiveValue implements Value {
    private final Signature reference;
    private final int offset;

    @Override
    public void load(MethodBuilder visitor, BuildData data) {
        if (!reference.isSimple()) {
            visitor.aLoad(offset);
        } else {
            visitor.varInsn(reference().getType(0).loadInsn(), offset);
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
