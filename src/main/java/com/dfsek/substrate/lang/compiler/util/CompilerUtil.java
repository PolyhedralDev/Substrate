package com.dfsek.substrate.lang.compiler.util;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class CompilerUtil implements Opcodes {
    public static String internalName(String clazz) {
        return clazz.replace('.', '/');
    }

    public static String internalName(Class<?> clazz) {
        return internalName(clazz.getCanonicalName());
    }

    public static void dump(String clazz, byte[] bytes, ZipOutputStream zipOutputStream) {
        if (zipOutputStream == null) return;
        try {
            zipOutputStream.putNextEntry(new ZipEntry(internalName(clazz) + ".class"));
            IOUtils.write(bytes, zipOutputStream);
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static ClassWriter generateClass(String name, boolean iface, String... ifaces) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

        writer.visit(V1_8,
                ACC_PUBLIC | (iface ? (ACC_ABSTRACT | ACC_INTERFACE) : 0),
                name,
                null,
                "java/lang/Object",
                ifaces);

        return writer;
    }

    public static String buildReturnType(BuildData data, Signature returnType) {
        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType)) + ";";
        } else if (!returnType.getSimpleReturn().isSimple()) {
            ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType.getSimpleReturn())) + ";";
        }
        return ret;
    }

    public static Signature expandArguments(List<ExpressionNode> arguments) {
        Signature argSignature;
        if (arguments.isEmpty()) {
            argSignature = Signature.empty();
        } else if (arguments.size() == 1) {
            argSignature = arguments.get(0).reference();
        } else {
            argSignature = arguments.get(0).reference();
            for (int i = 1; i < arguments.size(); i++) {
                argSignature = argSignature.and(arguments.get(i).reference());
            }
        }
        return argSignature;
    }

    public static void deconstructTuple(ExpressionNode node, BuildData data, MethodBuilder visitor) {
        Signature ref = node.reference();
        if (!ref.isSimple()) {
            data.offsetInc(1);
            int offset = data.getOffset();
            visitor.aStore(offset);

            for (int i = 0; i < ref.size(); i++) {
                visitor.aLoad(offset);

                visitor.invokeVirtual(CompilerUtil.internalName(data.tupleFactory().generate(ref)),
                        "param" + i,
                        "()" + ref.getType(i).descriptor());
            }
        }
    }
}
