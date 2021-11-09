package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public interface Value extends Opcodes {
    Signature reference();

    void load(MethodVisitor visitor, BuildData data);

    default boolean ephemeral() {
        return true;
    }
}
