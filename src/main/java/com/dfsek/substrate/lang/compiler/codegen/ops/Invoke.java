package com.dfsek.substrate.lang.compiler.codegen.ops;

import org.objectweb.asm.Opcodes;

public enum Invoke implements Bytecode {
    STATIC(Opcodes.INVOKESTATIC),
    INTERFACE(Opcodes.INVOKEINTERFACE),
    SPECIAL(Opcodes.INVOKESPECIAL),
    VIRTUAL(Opcodes.INVOKEVIRTUAL);
    private final int insn;

    Invoke(int insn) {
        this.insn = insn;
    }

    public int getCode() {
        return insn;
    }
}
