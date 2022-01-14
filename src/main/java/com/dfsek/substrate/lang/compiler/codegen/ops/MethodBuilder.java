package com.dfsek.substrate.lang.compiler.codegen.ops;

import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodBuilder implements Opcodes {
    private AtomicInteger localVariableIndex = new AtomicInteger(0);

    private final List<OpCode> opCodes = new ArrayList<>();
    private final List<Access> access = new ArrayList<>();

    private final Map<String, LocalVariable> localVariableMap = new HashMap<>();
    private final ClassBuilder classBuilder;

    private final String name, descriptor, signature;
    private final String[] exceptions;

    private final Deque<Signature> locals = new ArrayDeque<>();
    private final Deque<Signature> stack = new ArrayDeque<>();

    public MethodBuilder(ClassBuilder classBuilder, String name, String descriptor, String signature, String[] descriptions) {
        this.classBuilder = classBuilder;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = descriptions;
    }

    public MethodBuilder pushInt(int i) {
        opCodes.add(visitor -> {
            if (i == -1) {
                visitor.visitInsn(ICONST_M1);
            } else if (i == 0) {
                visitor.visitInsn(ICONST_0);
            } else if (i == 1) {
                visitor.visitInsn(ICONST_1);
            } else if (i == 2) {
                visitor.visitInsn(ICONST_2);
            } else if (i == 3) {
                visitor.visitInsn(ICONST_3);
            } else if (i == 4) {
                visitor.visitInsn(ICONST_4);
            } else if (i == 5) {
                visitor.visitInsn(ICONST_5);
            } else if (i >= -128 && i < 128) {
                visitor.visitIntInsn(BIPUSH, i); // byte
            } else if (i >= -32768 && i < 32768) {
                visitor.visitIntInsn(SIPUSH, i); // short
            } else {
                visitor.visitLdcInsn(i); // constant pool
            }
        });
        return this;
    }

    public MethodBuilder pushDouble(double value) {
        if(value == 0.0) {
            return insn(DCONST_0);
        } else if(value == 1.0) {
            return insn(DCONST_1);
        } else {
            return pushConst(value);
        }
    }

    public MethodBuilder pushConst(Object value) {
        return insn(visitor -> visitor.visitLdcInsn(value));
    }

    public MethodBuilder pop() {
        return insn(POP);
    }

    public MethodBuilder pop2() {
        return insn(POP2);
    }

    public MethodBuilder pop(int pop) {
        for (int i = pop; i > 0; i--) {
            if (i > 1) {
                pop2();
                i--; // extra decrement
            } else {
                pop(); // only 1 pop
            }
        }
        return this;
    }

    public MethodBuilder iLoad(int index) {
        return varInsn(ILOAD, index);
    }

    public MethodBuilder iStore(int index) {
        return varInsn(ISTORE, index);
    }

    public MethodBuilder dLoad(int index) {
        return varInsn(DLOAD, index);
    }

    public MethodBuilder dStore(int index) {
        return varInsn(DSTORE, index);
    }

    public MethodBuilder aLoad(int index) {
        return varInsn(ALOAD, index);
    }

    public MethodBuilder aStore(int index) {
        return varInsn(ASTORE, index);
    }

    public MethodBuilder insn(int opCode) {
        return insn(visitor -> visitor.visitInsn(opCode));
    }

    public MethodBuilder varInsn(int opCode, int var) {
        return insn(visitor -> visitor.visitVarInsn(opCode, var));
    }

    public MethodBuilder insn(OpCode opCode) {
        opCodes.add(opCode);
        return this;
    }

    public MethodBuilder invoke(Invoke type, String owner, String name, String descriptor, boolean isInterface) {
        return insn(visitor -> visitor.visitMethodInsn(type.insn, owner, name, descriptor, isInterface));
    }

    public MethodBuilder invokeVirtual(String owner, String name, String descriptor) {
        return invoke(Invoke.VIRTUAL, owner, name, descriptor, false);
    }

    public MethodBuilder invokeSpecial(String owner, String name, String descriptor) {
        return invoke(Invoke.SPECIAL, owner, name, descriptor, false);
    }

    public MethodBuilder invokeInterface(String owner, String name, String descriptor) {
        return invoke(Invoke.INTERFACE, owner, name, descriptor, true);
    }

    public MethodBuilder iAdd() {
        return insn(IADD);
    }

    public MethodBuilder iSub() {
        return insn(ISUB);
    }

    public MethodBuilder iMul() {
        return insn(IMUL);
    }

    public MethodBuilder iDiv() {
        return insn(IDIV);
    }

    public MethodBuilder dAdd() {
        return insn(DADD);
    }

    public MethodBuilder dSub() {
        return insn(DSUB);
    }

    public MethodBuilder dMul() {
        return insn(DMUL);
    }

    public MethodBuilder dDiv() {
        return insn(DDIV);
    }

    void apply(MethodVisitor visitor) {
        opCodes.forEach(opCode -> opCode.generate(visitor));
    }

    int access() {
        int a = 0;
        for (Access access : access) {
            a |= access.code;
        }

        return a;
    }

    public ClassBuilder classWriter() {
        return classBuilder;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public interface Bytecode {
        int getCode();
    }

    public enum Invoke implements  Bytecode {
        STATIC(INVOKESTATIC),
        INTERFACE(INVOKEINTERFACE),
        SPECIAL(INVOKESPECIAL),
        VIRTUAL(INVOKEVIRTUAL);
        private final int insn;

        Invoke(int insn) {
            this.insn = insn;
        }

        public int getCode() {
            return insn;
        }
    }

    public enum Access implements Bytecode {
        PUBLIC(ACC_PUBLIC),
        PRIVATE(ACC_PRIVATE),
        PROTECTED(ACC_PROTECTED),
        SYNTHETIC(ACC_SYNTHETIC),
        ABSTRACT(ACC_ABSTRACT),
        FINAL(ACC_FINAL);

        private final int code;

        Access(int code) {
            this.code = code;
        }

        @Override
        public int getCode() {
            return 0;
        }
    }
}
