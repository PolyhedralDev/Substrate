package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lexer.read.Position;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

public record PrimitiveValue(
        Signature reference,
        String id,
        int width
) implements Value {

    @Override
    public List<Either<CompileError, Op>> load(BuildData data, LinkedHashMap<String, Value> values) {
        return List.of(CompilerUtil.getOffset(values, id).fold(
                () -> Op.error("No such value \"" + id + "\"", Position.getNull()),
                offset -> reference
                        .loadInsn()
                        .bimap(
                                s -> Op.errorUnwrapped(s, Position.getNull()),
                                load -> Op.varInsnUnwrapped(load, offset)
                        )
        ));
    }

    @Override
    public int getLVWidth() {
        return width;
    }
}
