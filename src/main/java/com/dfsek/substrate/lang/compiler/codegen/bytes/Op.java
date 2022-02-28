package com.dfsek.substrate.lang.compiler.codegen.bytes;

import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public interface Op {
    void apply(MethodVisitor visitor);

    static Op insn(int insn) {
        return visitor -> visitor.visitInsn(insn);
    }

    static Op varInsn(int insn, int var) {
        return visitor -> visitor.visitVarInsn(insn, var);
    }

    static Op typeInsn(int insn, String type) {
        return visitor -> visitor.visitTypeInsn(insn, type);
    }

    // field insns
    static Op fieldInsn(
            MethodBuilder.Field field,
            String owner,
            String name,
            String descriptor) {
        return visitor -> visitor.visitFieldInsn(field.getCode(), owner, name, descriptor);
    }

    static Op getField(
            String owner,
            String name,
            String descriptor) {
        return fieldInsn(MethodBuilder.Field.GETFIELD, owner, name, descriptor);
    }

    static Op putField(
            String owner,
            String name,
            String descriptor) {
        return fieldInsn(MethodBuilder.Field.PUTFIELD, owner, name, descriptor);
    }

    static Op getStatic(
            String owner,
            String name,
            String descriptor) {
        return fieldInsn(MethodBuilder.Field.GETSTATIC, owner, name, descriptor);
    }

    static Op putStatic(
            String owner,
            String name,
            String descriptor) {
        return fieldInsn(MethodBuilder.Field.PUTSTATIC, owner, name, descriptor);
    }

    // int arithmetic
    Op IADD = insn(Opcodes.IADD);
    Op ISUB = insn(Opcodes.ISUB);
    Op IMUL = insn(Opcodes.IMUL);
    Op IDIV = insn(Opcodes.IDIV);
    Op IREM = insn(Opcodes.IREM);

    // double arithmetic
    Op DADD = insn(Opcodes.DADD);
    Op DSUB = insn(Opcodes.DSUB);
    Op DMUL = insn(Opcodes.DMUL);
    Op DDIV = insn(Opcodes.DDIV);
    Op DREM = insn(Opcodes.DREM);

    // load insns
    Op ILOAD = insn(Opcodes.ILOAD);
    Op DLOAD = insn(Opcodes.DLOAD);
    Op ALOAD = insn(Opcodes.ALOAD);

    // array load insns
    Op IALOAD = insn(Opcodes.IALOAD);
    Op DALOAD = insn(Opcodes.DALOAD);
    Op AALOAD = insn(Opcodes.AALOAD);

    // store insns
    Op ISTORE = insn(Opcodes.ISTORE);
    Op DSTORE = insn(Opcodes.DSTORE);
    Op ASTORE = insn(Opcodes.ASTORE);

    // array store insns
    Op IASTORE = insn(Opcodes.IASTORE);
    Op DASTORE = insn(Opcodes.DASTORE);
    Op AASTORE = insn(Opcodes.AASTORE);


}
