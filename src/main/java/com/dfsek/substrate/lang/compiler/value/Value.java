package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.Opcodes;

public interface Value extends Opcodes {
    Signature returnType();

    default boolean ephemeral() {
        return true;
    }
}
