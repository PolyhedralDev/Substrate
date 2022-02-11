package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.constant.ConstantExpressionNode;

public class BakedValue implements Value {
    private final ConstantExpressionNode<?> value;

    public BakedValue(ConstantExpressionNode<?> value) {
        this.value = value;
    }

    @Override
    public Signature reference() {
        return value.reference();
    }

    @Override
    public void load(MethodBuilder visitor, BuildData data) {
        value.apply(visitor, data);
    }
}
