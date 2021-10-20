package com.dfsek.substrate.lang.compiler;

import org.objectweb.asm.Opcodes;

public interface Value extends Opcodes {
    Signature returnType();

    default boolean ephemeral() {
        return true;
    }
}
