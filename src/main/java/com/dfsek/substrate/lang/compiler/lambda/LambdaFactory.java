package com.dfsek.substrate.lang.compiler.lambda;

import com.dfsek.substrate.lang.compiler.tuple.TupleFactory;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.pair.Pair;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.objectweb.asm.Opcodes.*;

public class LambdaFactory {
    private final Map<Signature, Map<Signature, Pair<Class<?>, AtomicInteger>>> generated = new HashMap<>();

    private final DynamicClassLoader classLoader;
    private final TupleFactory tupleFactory;

    public LambdaFactory(DynamicClassLoader classLoader, TupleFactory tupleFactory) {
        this.classLoader = classLoader;
        this.tupleFactory = tupleFactory;
    }

    public Class<?> generate(Signature args, Signature returnType) {
        return generated.computeIfAbsent(args, ignore -> new HashMap<>()).computeIfAbsent(returnType, ignore -> {
            ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES + org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
            String name = CompilerUtil.internalName(Lambda.class) + "IMPL_" + args.classDescriptor() + "$R" + returnType.classDescriptor();

            writer.visit(V1_8,
                    ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
                    name,
                    null,
                    "java/lang/Object",
                    new String[]{CompilerUtil.internalName(Lambda.class)});


            String ret = returnType.internalDescriptor();

            if (!returnType.isSimple()) {
                if (returnType.equals(Signature.empty())) ret = "V";
                else ret = "L" + CompilerUtil.internalName(tupleFactory.generate(returnType)) + ";";
            }

            MethodVisitor apply = writer.visitMethod(ACC_PUBLIC | ACC_ABSTRACT,
                    "apply",
                    "(" + args.internalDescriptor() + ")" + ret,
                    null,
                    null);
            apply.visitEnd();



            byte[] bytes = writer.toByteArray();
            Class<?> clazz = classLoader.defineClass(name.replace('/', '.'), bytes);
            CompilerUtil.dump(clazz, bytes);

            return Pair.of(clazz, new AtomicInteger(0));
        }).getLeft();
    }

    public Class<?> implement(Signature args, Signature returnType, BiConsumer<MethodVisitor, ClassWriter> consumer) {
        generate(args, returnType);

        Pair<Class<?>, AtomicInteger> pair = generated.get(args).get(returnType);

        ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES + org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        String name = CompilerUtil.internalName(Lambda.class) + "IMPL_" + args.classDescriptor() + "$R" + returnType.classDescriptor() + "$IM" + pair.getRight().getAndIncrement();

        writer.visit(V1_8,
                ACC_PUBLIC,
                name,
                null,
                "java/lang/Object",
                new String[]{CompilerUtil.internalName(pair.getLeft())});

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

        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(tupleFactory.generate(returnType)) + ";";
        }

        MethodVisitor apply = writer.visitMethod(ACC_PUBLIC,
                "apply",
                "(" + args.internalDescriptor() + ")" + ret,
                null,
                null);

        consumer.accept(apply, writer);
        apply.visitMaxs(0, 0);


        byte[] bytes = writer.toByteArray();

        Class<?> clazz = classLoader.defineClass(name.replace('/', '.'), bytes);
        CompilerUtil.dump(clazz, bytes);
        return clazz;
    }
}
