package com.dfsek.substrate.lang.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public interface Function extends Value {
    Signature arguments();

    void invoke(MethodVisitor visitor, BuildData data);

    default void generate(ClassWriter writer, BuildData data) {}
}
