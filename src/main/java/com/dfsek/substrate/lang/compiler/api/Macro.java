package com.dfsek.substrate.lang.compiler.api;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import org.objectweb.asm.Opcodes;

/**
 * a function-like construct which emits implementation specific bytecode.
 */
public interface Macro extends Opcodes, Typed {
    Signature arguments();

    default void prepare(MethodBuilder visitor) {
    }

    default boolean argsMatch(Signature attempt) {
        return arguments().equals(attempt);
    }

    void invoke(MethodBuilder visitor, BuildData data, Signature args);
}
