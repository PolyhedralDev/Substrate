package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import io.vavr.collection.List;
import io.vavr.control.Either;

public record InvalidValue(Signature reference, String id) implements Value{
    @Override
    public List<Either<CompileError, Op>> load(BuildData data) {
        throw new IllegalStateException("Invalid value: " + id);
    }
}
