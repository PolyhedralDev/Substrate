package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;

public class ThisReferenceValue implements Value {
    private final Signature ref;

    public ThisReferenceValue(Signature ref) {
        this.ref = ref;
    }

    @Override
    public Signature reference() {
        return ref;
    }

    @Override
    public void load(MethodBuilder visitor, BuildData data) {
        if (!ref.isSimple()) {
            visitor.aLoad(0);
        } else {
            visitor.varInsn(reference().getType(0).loadInsn(), 0);
        }
    }
}
