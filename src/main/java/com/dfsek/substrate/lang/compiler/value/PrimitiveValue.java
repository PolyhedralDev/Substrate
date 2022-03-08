package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lexer.read.Position;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class PrimitiveValue implements Value {
    private final Signature reference;
    private final int offset;

    public PrimitiveValue(Signature reference, int offset) {
        this.reference = reference;
        this.offset = offset;
    }

    @Override
    public List<Either<CompileError, Op>> load(BuildData data) {
        return List.of(reference
                .loadInsn()
                .bimap(
                        s -> Op.errorUnwrapped(s, Position.getNull()),
                        load -> Op.varInsnUnwrapped(load, offset)
                ));
    }

    @Override
    public Signature reference() {
        return reference;
    }

}
