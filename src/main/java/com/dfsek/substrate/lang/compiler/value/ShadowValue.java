package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;

public class ShadowValue implements Value {
    private final Signature signature;
    private final int field;

    public ShadowValue(Signature signature, int field) {
        this.signature = signature;
        this.field = field;
    }

    @Override
    public void load(MethodBuilder visitor, BuildData data) {
        visitor.aLoad(0);
        visitor.getField(
                data.getClassName(),
                "scope" + field,
                reference().internalDescriptor());
    }

    @Override
    public Signature reference() {
        return signature;
    }
}
