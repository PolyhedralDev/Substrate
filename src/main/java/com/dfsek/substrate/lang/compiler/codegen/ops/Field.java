package com.dfsek.substrate.lang.compiler.codegen.ops;

import org.objectweb.asm.Opcodes;

public enum Field implements Bytecode {
    GETFIELD(Opcodes.GETFIELD),
    PUTFIELD(Opcodes.PUTFIELD),
    GETSTATIC(Opcodes.GETSTATIC),
    PUTSTATIC(Opcodes.PUTSTATIC);

    private final int op;

    Field(int op) {
        this.op = op;
    }

    @Override
    public int getCode() {
        return op;
    }
}
