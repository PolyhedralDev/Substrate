package com.dfsek.substrate.lang.compiler.api;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import org.objectweb.asm.Opcodes;

/**
 * Static user defined function
 */
public interface Function extends Typed, Opcodes {
    Signature arguments();
    default void prepare(MethodBuilder visitor) {
    }

    void invoke(MethodBuilder visitor, BuildData data, Signature args);
}
