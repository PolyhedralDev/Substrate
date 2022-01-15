package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.ImplementationArguments;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
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
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class LambdaFactory {
    private final Map<Signature, Map<Signature, Pair<Class<?>, AtomicInteger>>> generated = new HashMap<>();

    private final DynamicClassLoader classLoader;
    private final TupleFactory tupleFactory;

    private static final String IMPL_ARG_CLASS_NAME = ImplementationArguments.class.getCanonicalName().replace('.', '/');

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
                    "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + ret,
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
        return LAMBDA_NAME + "IMPL_" + args.classDescriptor() + "$R" + returnType.classDescriptor() + "$IM" + (generated.get(args).get(returnType).getRight().get() - 1);
    }

    public void invoke(Signature args, Signature ret, BuildData data, MethodBuilder visitor) {

        visitor.invokeInterface(CompilerUtil.internalName(generate(args, ret)),
                "apply",
                "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + CompilerUtil.buildReturnType(data, ret));
    }

    public Class<?> implement(Signature args, Signature returnType, Signature scope, Consumer<MethodBuilder> consumer) {
        generate(args, returnType);

        Pair<Class<?>, AtomicInteger> pair = generated.get(args).get(returnType);

        String name = LAMBDA_NAME + "IMPL_" + args.classDescriptor() + "$R" + returnType.classDescriptor() + "$IM" + pair.getRight().getAndIncrement();

        ClassBuilder builder = new ClassBuilder(name, CompilerUtil.internalName(pair.getLeft()));

        MethodBuilder constructor = builder.method("<init>",
                        "(" + scope.internalDescriptor() + ")V")
                .access(MethodBuilder.Access.PUBLIC);


        constructor.aLoad(0)
                .invokeSpecial("java/lang/Object", "<init>", "()V");

        for (int i = 0; i < scope.size(); i++) {
            builder.field("scope" + i, scope.getType(i).descriptor(), MethodBuilder.Access.PRIVATE, MethodBuilder.Access.FINAL);

            constructor.aLoad(0)
                    .varInsn(scope.getType(i).loadInsn(), i + 1)
                    .putField(name, "scope" + i, scope.getType(i).descriptor());
        }

        constructor.voidReturn(); // Void return

        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(tupleFactory.generate(returnType)) + ";";
        }

        consumer.accept(builder.method("apply", "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() +")" + ret).access(MethodBuilder.Access.PUBLIC));

        return builder.build(classLoader);
    }
}
