package com.dfsek.substrate.lang.compiler.codegen.ops;

import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@SuppressWarnings("UnusedReturnValue")
public class MethodBuilder implements Opcodes {

    private final List<OpCode> opCodes = new ArrayList<>();
    private final List<Access> access = new ArrayList<>();

    private final ClassBuilder classBuilder;

    private final String name, descriptor, signature;
    private final String[] exceptions;

    public MethodBuilder(ClassBuilder classBuilder, String name, String descriptor, String signature, String[] descriptions) {
        this.classBuilder = classBuilder;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = descriptions;
    }

    public MethodBuilder access(Access access) {
        this.access.add(access);
        return this;
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
        if (value == 0.0) {
            return insn(DCONST_0);
        } else if (value == 1.0) {
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

    public MethodBuilder iinc(int lv, int inc) {
        return insn(visitor -> visitor.visitIincInsn(lv, inc));
    }

    public MethodBuilder dup() {
        return insn(DUP);
    }

    public MethodBuilder dup2() {
        return insn(DUP2);
    }

    public MethodBuilder arrayLength() {
        return insn(ARRAYLENGTH);
    }

    public MethodBuilder intInsn(int instruction, int op) {
        return insn(visitor -> visitor.visitIntInsn(instruction, op));
    }

    public MethodBuilder newArray(int type) {
        return intInsn(NEWARRAY, type);
    }

    public MethodBuilder aNewArray(String type) {
        return typeInsn(ANEWARRAY, type);
    }

    public MethodBuilder label(Label label) {
        return insn(visitor -> visitor.visitLabel(label));
    }

    public MethodBuilder jump(int insn, Label label) {
        return insn(visitor -> visitor.visitJumpInsn(insn, label));
    }

    public MethodBuilder goTo(Label label) {
        return jump(GOTO, label);
    }

    public MethodBuilder ifICmpLT(Label label) {
        return jump(IF_ICMPLT, label);
    }

    public MethodBuilder ifICmpGT(Label label) {
        return jump(IF_ICMPGT, label);
    }

    public MethodBuilder ifICmpLE(Label label) {
        return jump(IF_ICMPLE, label);
    }

    public MethodBuilder ifICmpGE(Label label) {
        return jump(IF_ICMPGE, label);
    }

    public MethodBuilder ifICmpEQ(Label label) {
        return jump(IF_ICMPEQ, label);
    }

    public MethodBuilder ifICmpNE(Label label) {
        return jump(IF_ICMPNE, label);
    }

    public MethodBuilder ifNE(Label label) {
        return jump(IFNE, label);
    }

    public MethodBuilder ifEQ(Label label) {
        return jump(IFEQ, label);
    }

    public MethodBuilder aaload() {
        return insn(AALOAD);
    }

    public MethodBuilder iaload() {
        return insn(IALOAD);
    }

    public MethodBuilder daload() {
        return insn(DASTORE);
    }

    public MethodBuilder aastore() {
        return insn(AALOAD);
    }

    public MethodBuilder iastore() {
        return insn(IASTORE);
    }

    public MethodBuilder dastore() {
        return insn(DASTORE);
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
        if (opCode == RETURN ||
                opCode == IRETURN ||
                opCode == DRETURN ||
                opCode == ARETURN ||
                opCode == LRETURN ||
                opCode == FRETURN) {
            return insn(new ReturnOpCode(opCode));
        }
        return insn(visitor -> visitor.visitInsn(opCode));
    }

    public MethodBuilder varInsn(int opCode, int var) {
        return insn(visitor -> visitor.visitVarInsn(opCode, var));
    }

    public MethodBuilder insn(OpCode opCode) {
        opCodes.add(opCode);
        return this;
    }

    public MethodBuilder i2d() {
        return insn(I2D);
    }

    public MethodBuilder d2i() {
        return insn(D2I);
    }

    public MethodBuilder invertBoolean() {
        Label caseTrue = new Label();
        Label caseFalse = new Label();
        ifNE(caseFalse);
        pushTrue();
        goTo(caseTrue);
        label(caseFalse);
        pushFalse();
        label(caseTrue);
        return this;
    }

    public MethodBuilder dcmpl() {
        return insn(DCMPL);
    }

    public MethodBuilder dcmpg() {
        return insn(DCMPG);
    }

    public MethodBuilder pushTrue() {
        return pushInt(1);
    }

    public MethodBuilder pushFalse() {
        return pushInt(0);
    }

    public MethodBuilder pushBoolean(boolean value) {
        if (value) pushInt(1);
        else pushInt(0);
        return this;
    }

    public MethodBuilder lineNumber(int number, Label label) {
        return insn(visitor -> visitor.visitLineNumber(number, label));
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

    public MethodBuilder invokeStatic(String owner, String name, String descriptor) {
        return invoke(Invoke.STATIC, owner, name, descriptor, false);
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

    public MethodBuilder newInsn(String type) {
        return typeInsn(NEW, type);
    }

    public MethodBuilder typeInsn(int insn, String type) {
        return insn(visitor -> visitor.visitTypeInsn(insn, type));
    }

    public MethodBuilder fieldInsn(Field field,
                                   java.lang.String owner,
                                   java.lang.String name,
                                   java.lang.String descriptor) {
        return insn(visitor -> visitor.visitFieldInsn(field.op, owner, name, descriptor));
    }

    public MethodBuilder getField(java.lang.String owner,
                                  java.lang.String name,
                                  java.lang.String descriptor) {
        return fieldInsn(Field.GETFIELD, owner, name, descriptor);
    }

    public MethodBuilder putField(java.lang.String owner,
                                  java.lang.String name,
                                  java.lang.String descriptor) {
        return fieldInsn(Field.PUTFIELD, owner, name, descriptor);
    }

    public MethodBuilder getStatic(java.lang.String owner,
                                   java.lang.String name,
                                   java.lang.String descriptor) {
        return fieldInsn(Field.GETSTATIC, owner, name, descriptor);
    }

    public MethodBuilder putStatic(java.lang.String owner,
                                   java.lang.String name,
                                   java.lang.String descriptor) {
        return fieldInsn(Field.PUTSTATIC, owner, name, descriptor);
    }

    public MethodBuilder voidReturn() {
        return insn(RETURN);
    }

    public MethodBuilder intReturn() {
        return insn(IRETURN);
    }

    public MethodBuilder doubleReturn() {
        return insn(DRETURN);
    }

    public MethodBuilder refReturn() {
        return insn(ARETURN);
    }


    void apply(MethodVisitor visitor) {
        opCodes.forEach(opCode -> opCode.generate(visitor));
        if (opCodes.isEmpty() || !(opCodes.get(opCodes.size() - 1) instanceof ReturnOpCode)) {
            visitor.visitInsn(RETURN); // return void if no return
        }
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

    public enum Invoke implements Bytecode {
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

    public enum Access implements Bytecode {
        PUBLIC(ACC_PUBLIC),
        PRIVATE(ACC_PRIVATE),
        PROTECTED(ACC_PROTECTED),
        SYNTHETIC(ACC_SYNTHETIC),
        ABSTRACT(ACC_ABSTRACT),
        FINAL(ACC_FINAL),
        STATIC(ACC_STATIC);

        private final int code;

        Access(int code) {
            this.code = code;
        }

        @Override
        public int getCode() {
            return code;
        }
    }

    private static class ReturnOpCode implements OpCode {
        private final int opCode;

        private ReturnOpCode(int opCode) {
            this.opCode = opCode;
        }

        @Override
        public void generate(MethodVisitor visitor) {
            visitor.visitInsn(opCode);
        }
    }
}
