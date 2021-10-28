package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;

public interface Function extends Value {
    Signature arguments();

    default boolean argsMatch(Signature attempt) {
        return arguments().equals(attempt);
    }

    default void preArgsPrep(MethodVisitor visitor, BuildData data) {
    }

    void invoke(MethodVisitor visitor, BuildData data, Signature args);

}
