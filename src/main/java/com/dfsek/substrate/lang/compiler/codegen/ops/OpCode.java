package com.dfsek.substrate.lang.compiler.codegen.ops;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public interface OpCode extends Opcodes {
    void generate(MethodVisitor visitor);
}
