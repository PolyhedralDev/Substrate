package com.dfsek.substrate.lang.compiler.api;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Opcodes;

/**
 * Static user defined function
 */
public interface Function extends Typed, Opcodes {
    Signature arguments();

    default List<Either<CompileError, Op>> prepare() {
        return List.empty();
    }

    List<Either<CompileError, Op>> invoke(BuildData data, Signature args);
}
