package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.constant.ConstantExpressionNode;
import io.vavr.collection.List;
import io.vavr.control.Either;

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
    public List<Either<CompileError, Op>> load(BuildData data) {
        return value.apply(data);
    }
}
