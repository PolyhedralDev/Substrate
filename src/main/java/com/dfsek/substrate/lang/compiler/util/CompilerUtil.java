package com.dfsek.substrate.lang.compiler.util;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class CompilerUtil implements Opcodes {
    public static String internalName(Class<?> clazz) {
        return clazz.getCanonicalName().replace('.', '/');
    }

    public static void dump(Class<?> clazz, byte[] bytes) {
        File dump = new File("./.substrate/dumps/" + internalName(clazz) + ".class");
        dump.getParentFile().mkdirs();
        System.out.println("Dumping to " + dump.getAbsolutePath());
        try {
            IOUtils.write(bytes, new FileOutputStream(dump));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void invertBoolean(MethodVisitor visitor) {
        Label caseTrue = new Label();
        Label caseFalse = new Label();
        visitor.visitJumpInsn(IFNE, caseFalse);
        visitor.visitInsn(ICONST_1);
        visitor.visitJumpInsn(GOTO, caseTrue);
        visitor.visitLabel(caseFalse);
        visitor.visitInsn(ICONST_0);
        visitor.visitLabel(caseTrue);
    }

    public static void pushInt(int i, MethodVisitor visitor) {
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
    }

    public static void invokeLambda(ExpressionNode lambdaContainer, MethodVisitor visitor, BuildData data) {
        ParserUtil.checkWeakReferenceType(lambdaContainer, data, Signature.fun());

        Signature returnType = lambdaContainer.reference(data).getSimpleReturn();

        Signature args = lambdaContainer.reference(data).getGenericArguments(0);

        data.lambdaFactory().invoke(args, returnType, data, visitor);
    }

    public static ClassWriter generateClass(String name, boolean iface, boolean defaultConstructor, String... ifaces) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

        writer.visit(V1_8,
                ACC_PUBLIC | (iface ? (ACC_ABSTRACT | ACC_INTERFACE) : 0),
                name,
                null,
                "java/lang/Object",
                ifaces);

        if(!iface & defaultConstructor) {
            MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                    "<init>", // Constructor method name is <init>
                    "()V",
                    null,
                    null);

            constructor.visitCode();
            constructor.visitVarInsn(ALOAD, 0); // Put this reference on stack
            constructor.visitMethodInsn(INVOKESPECIAL, // Invoke Object super constructor
                    "java/lang/Object",
                    "<init>",
                    "()V",
                    false);

            constructor.visitInsn(RETURN); // Void return
            constructor.visitMaxs(0, 0); // Set stack and local variable size (bogus values; handled automatically by ASM)

        }

        return writer;
    }

    public static String buildReturnType(BuildData data, Signature returnType) {
        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType)) + ";";
        } else if(!returnType.getSimpleReturn().isSimple()) {
            ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType.getSimpleReturn())) + ";";
        }
        return ret;
    }
}
