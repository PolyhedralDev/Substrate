package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;

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
    public void load(MethodBuilder visitor, BuildData data) {
        visitor.getStatic(data.getClassName(),
                "val" + field,
                type);
    }
}
