package com.dfsek.substrate.lang;

import org.objectweb.asm.MethodVisitor;

public interface BuildData {
    String generatedClassName();

    default void initialize(MethodVisitor visitor) {}

    default void finalize(MethodVisitor visitor) {}
}
