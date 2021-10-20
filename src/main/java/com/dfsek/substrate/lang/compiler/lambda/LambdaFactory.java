package com.dfsek.substrate.lang.compiler.lambda;

import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.tuple.TupleFactory;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.ReflectionUtil;
import com.dfsek.substrate.util.pair.ImmutablePair;
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
import java.util.function.BiConsumer;

import static org.objectweb.asm.Opcodes.*;

public class LambdaFactory {
    private final Map<Signature, Map<Signature, ImmutablePair<Class<?>, List<Class<?>>>>> generated = new HashMap<>();

    private final DynamicClassLoader classLoader;
    private final TupleFactory tupleFactory;

    public LambdaFactory(DynamicClassLoader classLoader, TupleFactory tupleFactory) {
        this.classLoader = classLoader;
        this.tupleFactory = tupleFactory;
    }

    public Class<?> generate(Signature args, Signature returnType) {
        return generated.computeIfAbsent(args, ignore -> new HashMap<>()).computeIfAbsent(returnType, ignore -> {
            ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES + org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
            String name = ReflectionUtil.internalName(Lambda.class) + "IMPL_" + args.classDescriptor() + "$" + returnType.classDescriptor();

            writer.visit(V1_8,
                    ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
                    name,
                    null,
                    "java/lang/Object",
                    new String[]{ReflectionUtil.internalName(Lambda.class)});


            String ret = returnType.internalDescriptor();

            if(!returnType.isSimple()) {
                ret = "L" + ReflectionUtil.internalName(tupleFactory.generate(returnType)) + ";";
            }

            MethodVisitor apply = writer.visitMethod(ACC_PUBLIC | ACC_ABSTRACT,
                    "apply",
                    "(" + args.internalDescriptor() + ")" + ret,
                    null,
                    null);
            apply.visitEnd();


            byte[] bytes = writer.toByteArray();

            if(true) {
                File dump = new File("./dumps/" + name + ".class");
                dump.getParentFile().mkdirs();
                System.out.println("Dumping to " + dump.getAbsolutePath());
                try {
                    IOUtils.write(bytes, new FileOutputStream(dump));
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            return ImmutablePair.of(classLoader.defineClass(name.replace('/', '.'), writer.toByteArray()), new ArrayList<>());
        }).getLeft();
    }

    public Class<?> implement(Signature args, Signature returnType, BiConsumer<MethodVisitor, ClassWriter> consumer) {
        generate(args, returnType);

        ImmutablePair<Class<?>, List<Class<?>>> pair = generated.get(args).get(returnType);

        ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES + org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        String name = ReflectionUtil.internalName(Lambda.class) + "IMPL_" + args.classDescriptor() + "$" + returnType.classDescriptor() + "$" + pair.getRight().size();

        writer.visit(V1_8,
                ACC_PUBLIC,
                name,
                null,
                "java/lang/Object",
                new String[]{ReflectionUtil.internalName(pair.getLeft())});

        String constructorSig = "(" + args.internalDescriptor() + ")V";

        MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                "<init>", // Constructor method name is <init>
                constructorSig,
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

        if(!returnType.isSimple()) {
            ret = "L" + ReflectionUtil.internalName(tupleFactory.generate(returnType)) + ";";
        }

        MethodVisitor apply = writer.visitMethod(ACC_PUBLIC,
                "apply",
                "(" + args.internalDescriptor() + ")" + ret,
                null,
                null);

        consumer.accept(apply, writer);
        apply.visitMaxs(0, 0);

        byte[] bytes = writer.toByteArray();

        if(true) {
            File dump = new File("./dumps/" + name + ".class");
            dump.getParentFile().mkdirs();
            System.out.println("Dumping to " + dump.getAbsolutePath());
            try {
                IOUtils.write(bytes, new FileOutputStream(dump));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        Class<?> clazz = classLoader.defineClass(name.replace('/', '.'), writer.toByteArray());
        pair.getRight().add(clazz);
        return clazz;
    }
}
