package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class ShadowValue implements Value {
    private final Signature signature;
    private final int field;

    public ShadowValue(Signature signature, int field) {
        this.signature = signature;
        this.field = field;
    }

    @Override
    public List<Either<CompileError, Op>> load(BuildData data) {
        return List.of(Op.aLoad(0))
                .append(Op.getField(
                        data.getClassName(),
                        "scope" + field,
                        reference().internalDescriptor()));
    }

    @Override
    public Signature reference() {
        return signature;
    }
}
