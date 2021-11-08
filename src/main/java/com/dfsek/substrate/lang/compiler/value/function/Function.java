package com.dfsek.substrate.lang.compiler.value.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public interface Function extends Typed, Opcodes {
    Signature arguments();

    default boolean argsMatch(Signature attempt) {
        return arguments().equals(attempt);
    }

    default void prepare(MethodVisitor visitor) {
    }

    void invoke(MethodVisitor visitor, BuildData data, Signature args);
}
