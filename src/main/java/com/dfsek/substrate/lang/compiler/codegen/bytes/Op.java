package com.dfsek.substrate.lang.compiler.codegen.bytes;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.ops.Field;
import com.dfsek.substrate.lang.compiler.codegen.ops.Invoke;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lexer.read.Position;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;


public interface Op {
    static Either<CompileError, Op> iinc(int lv, int inc) {
        return Either.right(visitor -> visitor.visitIincInsn(lv, inc));
    }

    static Either<CompileError, Op> arrayLength() {
        return insn(ARRAYLENGTH);
    }

    static Either<CompileError, Op> insn(int insn) {
        return Either.right(insnUnwrapped(insn));
    }

    static Op insnUnwrapped(int insn) {
        return visitor -> visitor.visitInsn(insn);
    }

    static Either<CompileError, Op> varInsn(int insn, int var) {
        return Either.right(varInsnUnwrapped(insn, var));
    }

    static Op varInsnUnwrapped(int insn, int var) {
        return visitor -> visitor.visitVarInsn(insn, var);
    }

    static Either<CompileError, Op> intInsn(int insn, int var) {
        return Either.right(visitor -> visitor.visitIntInsn(insn, var));
    }

    static Either<CompileError, Op> typeInsn(int insn, String type) {
        return Either.right(visitor -> visitor.visitTypeInsn(insn, type));
    }

    static Either<CompileError, Op> jumpInsn(int insn, Label jump) {
        return Either.right(visitor -> visitor.visitJumpInsn(insn, jump));
    }

    static Either<CompileError, Op> pushConst(Object value) {
        return Either.right(visitor -> visitor.visitLdcInsn(value));
    }

    static Either<CompileError, Op> label(Label label) {
        return Either.right(visitor -> visitor.visitLabel(label));
    }

    // field insns
    static Either<CompileError, Op> fieldInsn(
            Field field,
            String owner,
            String name,
            String descriptor) {
        return Either.right(visitor -> visitor.visitFieldInsn(field.getCode(), owner, name, descriptor));
    }

    static Either<CompileError, Op> getField(
            String owner,
            String name,
            String descriptor) {
        return fieldInsn(Field.GETFIELD, owner, name, descriptor);
    }

    static Either<CompileError, Op> putField(
            String owner,
            String name,
            String descriptor) {
        return fieldInsn(Field.PUTFIELD, owner, name, descriptor);
    }

    static Either<CompileError, Op> getStatic(
            String owner,
            String name,
            String descriptor) {
        return fieldInsn(Field.GETSTATIC, owner, name, descriptor);
    }

    static Either<CompileError, Op> putStatic(
            String owner,
            String name,
            String descriptor) {
        return fieldInsn(Field.PUTSTATIC, owner, name, descriptor);
    }

    static Either<CompileError, Op> invoke(Invoke type, String owner, String name, String descriptor, boolean isInterface) {
        return Either.right(visitor -> visitor.visitMethodInsn(type.getCode(), owner, name, descriptor, isInterface));
    }

    static Either<CompileError, Op> invokeVirtual(String owner, String name, String descriptor) {
        return invoke(Invoke.VIRTUAL, owner, name, descriptor, false);
    }

    static Either<CompileError, Op> invokeSpecial(String owner, String name, String descriptor) {
        return invoke(Invoke.SPECIAL, owner, name, descriptor, false);
    }

    static Either<CompileError, Op> invokeStatic(String owner, String name, String descriptor) {
        return invoke(Invoke.STATIC, owner, name, descriptor, false);
    }

    static Either<CompileError, Op> invokeStaticInterface(String owner, String name, String descriptor) {
        return invoke(Invoke.STATIC, owner, name, descriptor, true);
    }

    static Either<CompileError, Op> invokeInterface(String owner, String name, String descriptor) {
        return invoke(Invoke.INTERFACE, owner, name, descriptor, true);
    }

    // comparisons
    static Either<CompileError, Op> dcmpl() {
        return insn(DCMPL);
    }

    // if
    static Either<CompileError, Op> ifNE(Label label) {
        return jumpInsn(IFNE, label);
    }

    static Either<CompileError, Op> ifEQ(Label label) {
        return jumpInsn(IFEQ, label);
    }

    static Either<CompileError, Op> ifICmpGE(Label label) {
        return jumpInsn(IF_ICMPGE, label);
    }

    static Either<CompileError, Op> goTo(Label label) {
        return jumpInsn(GOTO, label);
    }

    // int arithmetic
    static Either<CompileError, Op> iAdd() {
        return insn(IADD);
    }

    static Either<CompileError, Op> iSub() {
        return insn(ISUB);
    }

    static Either<CompileError, Op> iMul() {
        return insn(IMUL);
    }

    static Either<CompileError, Op> iDiv() {
        return insn(IDIV);
    }

    static Either<CompileError, Op> iRem() {
        return insn(IREM);
    }

    static Either<CompileError, Op> iNeg() {
        return insn(INEG);
    }

    static Either<CompileError, Op> dAdd() {
        return insn(DADD);
    }

    // double arithmetic

    static Either<CompileError, Op> dSub() {
        return insn(DSUB);
    }

    static Either<CompileError, Op> dMul() {
        return insn(DMUL);
    }

    static Either<CompileError, Op> dDiv() {
        return insn(DDIV);
    }

    static Either<CompileError, Op> dRem() {
        return insn(DREM);
    }

    static Either<CompileError, Op> dNeg() {
        return insn(DNEG);
    }

    static Either<CompileError, Op> i2d() {
        return insn(I2D);
    }

    // casts

    static Either<CompileError, Op> d2i() {
        return insn(D2I);
    }

    static Either<CompileError, Op> iLoad(int i) {
        return varInsn(ILOAD, i);
    }

    // load insns

    static Either<CompileError, Op> dLoad(int i) {
        return varInsn(DLOAD, i);
    }

    static Either<CompileError, Op> aLoad(int i) {
        return varInsn(ALOAD, i);
    }

    // array load insns
    static Either<CompileError, Op> iaLoad() {
        return insn(IALOAD);
    }

    static Either<CompileError, Op> daLoad() {
        return insn(DALOAD);
    }

    static Either<CompileError, Op> aaLoad() {
        return insn(AALOAD);
    }

    // store insns
    static Either<CompileError, Op> iStore(int i) {
        return varInsn(ISTORE, i);
    }

    static Either<CompileError, Op> dStore(int i) {
        return varInsn(DSTORE, i);
    }

    static Either<CompileError, Op> aStore(int i) {
        return varInsn(ASTORE, i);
    }

    // array store insns
    static Either<CompileError, Op> iaStore() {
        return insn(IASTORE);
    }

    static Either<CompileError, Op> daStore() {
        return insn(DASTORE);
    }

    static Either<CompileError, Op> aaStore() {
        return insn(AASTORE);
    }

    // helpers
    static List<Either<CompileError, Op>> invertBoolean() {
        Label caseTrue = new Label();
        Label caseFalse = new Label();
        return List.of(
                ifNE(caseFalse),
                pushTrue(),
                goTo(caseTrue),
                label(caseFalse),
                pushFalse(),
                label(caseTrue)
        );
    }

    static Either<CompileError, Op> pushInt(int i) {
        return Either.right(visitor -> {
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
    }

    static Either<CompileError, Op> pushTrue() {
        return pushInt(1);
    }

    static Either<CompileError, Op> pushFalse() {
        return pushInt(0);
    }

    static Either<CompileError, Op> pushDouble(double value) {
        if (value == 0.0) {
            return insn(DCONST_0);
        } else if (value == 1.0) {
            return insn(DCONST_1);
        } else {
            return pushConst(value);
        }
    }

    static Either<CompileError, Op> pop() {
        return insn(POP);
    }

    static Either<CompileError, Op> pop2() {
        return insn(POP2);
    }

    static Either<CompileError, Op> pop(Signature signature) {
        if (signature.equals(Signature.decimal())) return pop2();
        return pop();
    }

    static Either<CompileError, Op> dup() {
        return insn(DUP);
    }

    static Either<CompileError, Op> dup2() {
        return insn(DUP2);
    }

    static Either<CompileError, Op> dup(Signature signature) {
        if (signature.equals(Signature.decimal())) return dup2();
        return dup();
    }

    static Either<CompileError, Op> aReturn() {
        return insn(ARETURN);
    }

    // type insns
    static Either<CompileError, Op> newInsn(String type) {
        return typeInsn(NEW, type);
    }

    static Either<CompileError, Op> checkCast(String type) {
        return typeInsn(CHECKCAST, type);
    }

    static Either<CompileError, Op> newArray(int type) {
        return intInsn(NEWARRAY, type);
    }

    static Either<CompileError, Op> aNewArray(String type) {
        return typeInsn(ANEWARRAY, type);
    }

    static List<Either<CompileError, Op>> getValue(LinkedHashMap<String, Value> valueMap, BuildData data, String name, Position position) {
        return valueMap.get(name)
                .fold(() -> List.of(error("No such value \"" + name + "\"", position)), value -> value.load(data));
    }

    static Either<CompileError, Op> error(String message, Position position) {
        return Either.left(errorUnwrapped(message, position, new Exception()));
    }

    static Either<CompileError, Op> error(String message, Position position, Exception e) {
        return Either.left(errorUnwrapped(message, position, e));
    }

    static CompileError errorUnwrapped(String message, Position position, Exception e) {
        return new CompileError() {
            @Override
            public String message() {
                return message;
            }

            @Override
            public void dumpStack() {
                e.printStackTrace();
            }

            @Override
            public Position getPosition() {
                return position;
            }
        };
    }

    static CompileError errorUnwrapped(String message, Position position) {
        return errorUnwrapped(message, position, new Exception());
    }

    static Either<CompileError, Op> nothing() {
        return Either.right(v -> {
        });
    }

    void apply(MethodVisitor visitor);
}
