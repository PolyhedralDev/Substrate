package com.dfsek.substrate.lang.compiler.util;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.Classes;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class CompilerUtil implements Opcodes {
    public static String internalName(String clazz) {
        return clazz.replace('.', '/');
    }

    public static String internalName(Class<?> clazz) {
        return Type.getInternalName(clazz);
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

    public static ClassWriter generateClass(String name, String superClass, boolean iface, String... ifaces) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

        writer.visit(V1_8,
                ACC_PUBLIC | (iface ? (ACC_ABSTRACT | ACC_INTERFACE) : 0),
                name,
                null,
                superClass,
                ifaces);

        return writer;
    }

    public static String buildReturnType(BuildData data, Signature returnType) {
        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType).clazz()) + ";";
        } else if (!returnType.getSimpleReturn().isSimple()) {
            ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType.getSimpleReturn()).clazz()) + ";";
        }
        return ret;
    }

    public static Signature expandArguments(List<? extends Typed> arguments) {
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

    public static io.vavr.collection.List<Either<CompileError, Op>> deconstructTuple(ExpressionNode node, BuildData data) {
        Signature ref = node.reference();
        if (!ref.isSimple()) {
            data.offsetInc(1);
            int offset = data.getOffset();
            io.vavr.collection.List<Either<CompileError, Op>> list = io.vavr.collection.List.of(Op.aStore(offset));

            for (int i = 0; i < ref.size(); i++) {
                list = list
                        .append(Op.aLoad(offset))
                        .append(data.tupleFactory().get(ref, i));
            }
            return list;
        }
        return io.vavr.collection.List.empty();
    }


    public static int getTotalOffset(LinkedHashMap<String, Value> values) {
        return values.values().foldLeft(0, (i, v) -> i + v.getLVWidth());
    }

    public static Option<Integer> getOffset(LinkedHashMap<String, Value> values, String id) {
        return values
                .get(id)
                .map(value -> values.takeUntil(v -> v._1.equals(id)).values().foldLeft(0, (i, v) -> i + v.getLVWidth()) + value.getLVWidth());
    }

    public static io.vavr.collection.List<Either<CompileError, Op>> box(Typed typed) {
        return box(typed.reference());
    }

    public static io.vavr.collection.List<Either<CompileError, Op>> box(Signature ref) {
        if(ref.equals(Signature.integer())) {
            return List.of(Op.invokeStatic(Classes.INTEGER, "valueOf", "(I)L" + Classes.INTEGER + ";"));
        } else if(ref.equals(Signature.decimal())) {
            return List.of(Op.invokeStatic(Classes.DOUBLE, "valueOf", "(D)L" + Classes.DOUBLE + ";"));
        }
        else return List.empty();
    }
    public static io.vavr.collection.List<Either<CompileError, Op>> unbox(Typed typed) {
        return unbox(typed.reference());
    }

    public static io.vavr.collection.List<Either<CompileError, Op>> unbox(Signature ref) {
        if(ref.equals(Signature.integer())) {
            return List.of(
                    Op.checkCast(Classes.INTEGER),
                    Op.invokeVirtual(Classes.INTEGER, "intValue", "()I"));
        } else if(ref.equals(Signature.decimal())) {
            return List.of(
                    Op.checkCast(Classes.DOUBLE),
                    Op.invokeVirtual(Classes.DOUBLE, "doubleValue", "()D"));
        }
        else return List.empty();
    }
}
