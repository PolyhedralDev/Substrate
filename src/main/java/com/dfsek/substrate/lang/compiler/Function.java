package com.dfsek.substrate.lang.compiler;

import jdk.internal.org.objectweb.asm.ClassWriter;

public interface Function extends Value {
    Signature arguments();

    default void generate(ClassWriter writer, BuildData data) {}
}
