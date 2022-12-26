package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import io.vavr.collection.List;
import io.vavr.control.Either;

public record ShadowValue(
        Signature signature,
        int field,

        String id,

        String clazz
) implements Value {

    @Override
    public List<Either<CompileError, Op>> load(BuildData data) {
        return List.of(Op.aLoad(0))
                .append(Op.getField(
                        CompilerUtil.internalName(clazz),
                        "scope" + field,
                        reference().internalDescriptor()));
    }

    @Override
    public Signature reference() {
        return signature;
    }
}
