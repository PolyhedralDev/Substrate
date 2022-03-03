package com.dfsek.substrate.lang.compiler.codegen.ops;

import org.objectweb.asm.Opcodes;

public enum Access implements Bytecode {
    PUBLIC(Opcodes.ACC_PUBLIC),
    PRIVATE(Opcodes.ACC_PRIVATE),
    PROTECTED(Opcodes.ACC_PROTECTED),
    SYNTHETIC(Opcodes.ACC_SYNTHETIC),
    ABSTRACT(Opcodes.ACC_ABSTRACT),
    FINAL(Opcodes.ACC_FINAL),
    STATIC(Opcodes.ACC_STATIC);

    private final int code;

    Access(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
