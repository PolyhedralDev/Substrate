package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Opcodes;

public interface Value extends Opcodes {
    Signature reference();

    List<Either<CompileError, Op>> load(BuildData data);

    default int getLVWidth() {
        return 0;
    }
}
