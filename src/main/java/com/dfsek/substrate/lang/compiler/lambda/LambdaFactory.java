package com.dfsek.substrate.lang.compiler.lambda;

import com.dfsek.substrate.lang.compiler.tuple.TupleFactory;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.pair.Pair;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.objectweb.asm.Opcodes.*;

public class LambdaFactory {
    private final Map<Signature, Map<Signature, Pair<Class<?>, AtomicInteger>>> generated = new HashMap<>();

    private final DynamicClassLoader classLoader;
    private final TupleFactory tupleFactory;

    private static final String LAMBDA_NAME = CompilerUtil.internalName(Lambda.class);

    public LambdaFactory(DynamicClassLoader classLoader, TupleFactory tupleFactory) {
        this.classLoader = classLoader;
        this.tupleFactory = tupleFactory;
    }

    public Class<?> generate(Signature args, Signature returnType) {
        return generated.computeIfAbsent(args, ignore -> new HashMap<>()).computeIfAbsent(returnType, ignore -> {
            String name = LAMBDA_NAME + "$" + args.classDescriptor() + "$R" + returnType.classDescriptor();
            ClassWriter writer = CompilerUtil.generateClass(name, true, false, LAMBDA_NAME);

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

    public String name(Signature args, Signature returnType) {
        generate(args, returnType);
        return LAMBDA_NAME + "IMPL_" + args.classDescriptor() + "$R" + returnType.classDescriptor() + "$IM" + (generated.get(args).get(returnType).getRight().get());
    }

    public Class<?> implement(Signature args, Signature returnType, Signature scope, BiConsumer<MethodVisitor, ClassWriter> consumer) {
        generate(args, returnType);

        System.out.println("scope: " + scope);

        Pair<Class<?>, AtomicInteger> pair = generated.get(args).get(returnType);

        String name = LAMBDA_NAME + "IMPL_" + args.classDescriptor() + "$R" + returnType.classDescriptor() + "$IM" + pair.getRight().getAndIncrement();

        ClassWriter writer = CompilerUtil.generateClass(name, false, false, CompilerUtil.internalName(pair.getLeft()));

        MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                "<init>", // Constructor method name is <init>
                "(" + scope.internalDescriptor() + ")V",
                null,
                null);

        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0); // Put this reference on stack
        constructor.visitMethodInsn(INVOKESPECIAL, // Invoke Object super constructor
                "java/lang/Object",
                "<init>",
                "()V",
                false);

        for (int i = 0; i < scope.size(); i++) {
            writer.visitField(ACC_PRIVATE | ACC_FINAL,
                    "scope" + i,
                    scope.getType(i).descriptor(),
                    null,
                    null);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitVarInsn(scope.getType(i).loadInsn(), i + 1);
            constructor.visitFieldInsn(PUTFIELD,
                    name,
                    "scope" + i,
                    scope.getType(i).descriptor());
        }

        constructor.visitInsn(RETURN); // Void return
        constructor.visitMaxs(0, 0);


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
