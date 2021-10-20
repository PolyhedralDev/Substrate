package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public interface Function extends Value {
    Signature arguments();

    default void preArgsPrep(MethodVisitor visitor, BuildData data) {}

    void invoke(MethodVisitor visitor, BuildData data);

    default void generate(ClassWriter writer, BuildData data) {}
}
