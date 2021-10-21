package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public interface Function extends Value {
    Signature arguments();

    @Override
    default Signature reference() {
        return Signature.fun();
    }

    default void preArgsPrep(MethodVisitor visitor, BuildData data) {
    }

    void invoke(MethodVisitor visitor, BuildData data);

    default void generate(ClassWriter writer, BuildData data) {
    }
}
