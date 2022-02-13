package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.Opcodes;

public interface Value extends Opcodes {
    Signature reference();

    void load(MethodBuilder visitor, BuildData data);
}
