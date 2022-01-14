package com.dfsek.substrate.lang.compiler.util;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
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
import java.util.List;

public final class CompilerUtil implements Opcodes {
    public static String internalName(String clazz) {
        return clazz.replace('.', '/');
    }

    public static String internalName(Class<?> clazz) {
        return internalName(clazz.getCanonicalName());
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

    public static void invokeLambda(ExpressionNode lambdaContainer, MethodBuilder visitor, BuildData data) {
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

    public static Signature expandArguments(BuildData data, List<ExpressionNode> arguments) {
        Signature argSignature;
        if (arguments.isEmpty()) {
            argSignature = Signature.empty();
        } else if (arguments.size() == 1) {
            argSignature = arguments.get(0).reference(data).expandTuple();
        } else {
            argSignature = arguments.get(0).reference(data).expandTuple();
            for (int i = 1; i < arguments.size(); i++) {
                argSignature = argSignature.and(arguments.get(i).reference(data).expandTuple());
            }
        }
        return argSignature;
    }

    public static void deconstructTuple(ExpressionNode node, BuildData data, MethodBuilder visitor) {
        Signature ref = node.reference(data);
        if (ref.weakEquals(Signature.tup())) {
            data.offsetInc(1);
            int offset = data.getOffset();
            visitor.aStore(offset);

            Signature tup = ref.expandTuple();

            for (int i = 0; i < tup.size(); i++) {
                visitor.aLoad(offset);

                visitor.invokeVirtual(
                        CompilerUtil.internalName(data.tupleFactory().generate(tup)),
                        "param" + i,
                        "()" + tup.getType(i).descriptor());
            }

        }
    }
}
